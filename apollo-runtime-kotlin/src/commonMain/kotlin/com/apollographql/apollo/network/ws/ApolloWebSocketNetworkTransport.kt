package com.apollographql.apollo.network.ws

import com.apollographql.apollo.exception.ApolloWebSocketException
import com.apollographql.apollo.exception.ApolloWebSocketServerException
import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ExecutionContext
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Subscription
import com.apollographql.apollo.api.internal.json.JsonWriter
import com.apollographql.apollo.api.internal.json.Utils
import com.apollographql.apollo.api.parse
import com.apollographql.apollo.dispatcher.ApolloCoroutineDispatcherContext
import com.apollographql.apollo.exception.ApolloParseException
import com.apollographql.apollo.ApolloRequest
import com.apollographql.apollo.interceptor.ApolloResponse
import com.apollographql.apollo.network.NetworkTransport
import com.apollographql.apollo.subscription.ApolloOperationMessageSerializer
import com.apollographql.apollo.subscription.OperationClientMessage
import com.apollographql.apollo.subscription.OperationMessageSerializer
import com.apollographql.apollo.subscription.OperationMessageSerializer.Companion.toByteString
import com.apollographql.apollo.subscription.OperationServerMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import okio.Buffer
import okio.ByteString

@ApolloExperimental
@ExperimentalCoroutinesApi
/**
 * Apollo GraphQL WS protocol implementation:
 * https://github.com/apollographql/subscriptions-transport-ws/blob/master/PROTOCOL.md
 */
class ApolloWebSocketNetworkTransport(
    private val webSocketFactory: WebSocketFactory,
    private val connectionParams: Map<String, Any?> = emptyMap(),
    private val connectionAcknowledgeTimeoutMs: Long = 10_000,
    private val idleTimeoutMs: Long = 60_000,
    private val connectionKeepAliveTimeoutMs: Long = -1,
    private val serializer: OperationMessageSerializer = ApolloOperationMessageSerializer
) : NetworkTransport {
  private val mutex = Mutex()
  private var graphQLWebsocketConnection: GraphQLWebsocketConnection? = null

  override fun <D : Operation.Data> execute(
      request: ApolloRequest<D>,
      responseAdapterCache: ResponseAdapterCache,
      executionContext: ExecutionContext
  ): Flow<ApolloResponse<D>> {
    val dispatcherContext = requireNotNull(
        executionContext[ApolloCoroutineDispatcherContext] ?: request.executionContext[ApolloCoroutineDispatcherContext]
    )
    return getServerConnection(dispatcherContext).flatMapLatest { serverConnection ->
      serverConnection
          .subscribe()
          .filter { message -> message !is OperationServerMessage.ConnectionAcknowledge }
          .takeWhile { message -> message !is OperationServerMessage.Complete || message.id != request.requestUuid.toString() }
          .mapNotNull { message -> message.process(request, responseAdapterCache) }
          .onStart {
            serverConnection.send(
                OperationClientMessage.Start(
                    subscriptionId = request.requestUuid.toString(),
                    subscription = request.operation as Subscription<*>,
                    responseAdapterCache = responseAdapterCache,
                    autoPersistSubscription = false,
                    sendSubscriptionDocument = true
                )
            )
          }.onCompletion { cause ->
            if (cause == null) {
              serverConnection.send(
                  OperationClientMessage.Stop(request.requestUuid.toString())
              )
            }
          }
    }
  }

  private fun <D : Operation.Data> OperationServerMessage.process(
      request: ApolloRequest<D>,
      responseAdapterCache: ResponseAdapterCache
  ): ApolloResponse<D>? {
    return when (this) {
      is OperationServerMessage.Error -> {
        if (id == request.requestUuid.toString()) {
          throw ApolloWebSocketServerException(
              message = "Failed to execute GraphQL operation",
              payload = payload
          )
        }
        null
      }

      is OperationServerMessage.Data -> {
        if (id == request.requestUuid.toString()) {
          val buffer = Buffer().apply {
            JsonWriter.of(buffer)
                .apply { Utils.writeToJson(payload, this) }
                .flush()
          }

          val response = try {
            request.operation.parse(
                source = buffer,
                responseAdapterCache = responseAdapterCache
            )
          } catch (e: Exception) {
            throw ApolloParseException(
                message = "Failed to parse GraphQL network response",
                cause = e
            )
          }

          ApolloResponse(
              requestUuid = request.requestUuid,
              response = response,
              executionContext = ExecutionContext.Empty
          )
        } else null
      }

      else -> null
    }
  }

  private fun getServerConnection(dispatcherContext: ApolloCoroutineDispatcherContext): Flow<GraphQLWebsocketConnection> {
    return flow {
      val connection = mutex.withLock {
        if (graphQLWebsocketConnection?.isClosedForReceive() != false) {
          graphQLWebsocketConnection = openServerConnection(dispatcherContext)
        }
        graphQLWebsocketConnection
      }
      emit(connection)
    }.filterNotNull()
  }

  private suspend fun openServerConnection(dispatcherContext: ApolloCoroutineDispatcherContext): GraphQLWebsocketConnection {
    return try {
      withTimeout(connectionAcknowledgeTimeoutMs) {
        val webSocketConnection = webSocketFactory.open(
            mapOf(
                "Sec-WebSocket-Protocol" to "graphql-ws"
            )
        )
        webSocketConnection.send(OperationClientMessage.Init(connectionParams).toByteString(serializer))
        while (serializer.readServerMessage(Buffer().write(webSocketConnection.receive()))
                !is OperationServerMessage.ConnectionAcknowledge) {
          // await for connection acknowledgement
        }
        GraphQLWebsocketConnection(
            webSocketConnection = webSocketConnection,
            idleTimeoutMs = idleTimeoutMs,
            connectionKeepAliveTimeoutMs = connectionKeepAliveTimeoutMs,
            defaultDispatcher = dispatcherContext.default,
            serializer = serializer
        )
      }
    } catch (e: TimeoutCancellationException) {
      throw ApolloWebSocketException(
          message = "Failed to establish GraphQL websocket connection with the server, timeout.",
          cause = e
      )
    } catch (e: Exception) {
      throw ApolloWebSocketException(
          message = "Failed to establish GraphQL websocket connection with the server.",
          cause = e
      )
    }
  }

  private class GraphQLWebsocketConnection(
      val webSocketConnection: WebSocketConnection,
      val idleTimeoutMs: Long,
      val connectionKeepAliveTimeoutMs: Long,
      private val serializer: OperationMessageSerializer,
      defaultDispatcher: CoroutineDispatcher
  ) {
    private val messageChannel: BroadcastChannel<ByteString> = webSocketConnection.broadcast(Channel.CONFLATED)
    private val coroutineScope = CoroutineScope(SupervisorJob() + defaultDispatcher)
    private val mutex = Mutex()
    private var activeSubscriptionCount = 0
    private var idleTimeoutJob: Job? = null
    private var connectionKeepAliveTimeoutJob: Job? = null

    suspend fun isClosedForReceive(): Boolean {
      return mutex.withLock {
        webSocketConnection.isClosedForReceive
      }
    }

    fun send(message: OperationClientMessage) {
      webSocketConnection.send(message.toByteString(serializer))
    }

    fun subscribe(): Flow<OperationServerMessage> {
      return messageChannel.openSubscription()
          .consumeAsFlow()
          .onStart { onSubscribed() }
          .onCompletion { onUnsubscribed() }
          .map { data -> serializer.readServerMessage(Buffer().write(data)) }
          .onEach { message ->
            if (message is OperationServerMessage.ConnectionKeepAlive) onConnectionKeepAlive()
          }
    }

    private suspend fun onSubscribed() {
      mutex.withLock {
        activeSubscriptionCount++
        idleTimeoutJob?.cancel()
      }
    }

    private suspend fun onUnsubscribed() {
      mutex.withLock {
        if (--activeSubscriptionCount == 0 && idleTimeoutMs > 0) {
          idleTimeoutJob?.cancel()
          connectionKeepAliveTimeoutJob?.cancel()

          idleTimeoutJob = coroutineScope.launch {
            delay(idleTimeoutMs)
            close()
          }
        }
      }
    }

    private suspend fun onConnectionKeepAlive() {
      mutex.withLock {
        if (activeSubscriptionCount > 0 && connectionKeepAliveTimeoutMs > 0) {
          connectionKeepAliveTimeoutJob?.cancel()

          connectionKeepAliveTimeoutJob = coroutineScope.launch {
            delay(idleTimeoutMs)
            close()
          }
        }
      }
    }

    private suspend fun close() {
      mutex.withLock {
        webSocketConnection.close()
      }
    }
  }
}
