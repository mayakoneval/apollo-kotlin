// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.deprecation

import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.InputFieldMarshaller
import com.apollographql.apollo.api.internal.QueryDocumentMinifier
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.deprecation.adapter.TestQuery_ResponseAdapter
import com.example.deprecation.type.Episode
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.Transient

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
data class TestQuery(
  val episode: Input<Episode> = Input.absent()
) : Query<TestQuery.Data> {
  @Transient
  private val variables: Operation.Variables = object : Operation.Variables() {
    override fun valueMap(): Map<String, Any?> = mutableMapOf<String, Any?>().apply {
      if (this@TestQuery.episode.defined) {
        this["episode"] = this@TestQuery.episode.value
      }
    }

    override fun marshaller(): InputFieldMarshaller {
      return InputFieldMarshaller.invoke { writer ->
        if (this@TestQuery.episode.defined) {
          writer.writeString("episode", this@TestQuery.episode.value?.rawValue)
        }
      }
    }
  }

  override fun operationId(): String = OPERATION_ID

  override fun queryDocument(): String = QUERY_DOCUMENT

  override fun variables(): Operation.Variables = variables

  override fun name(): String = OPERATION_NAME

  override fun adapter(customScalarAdapters: ResponseAdapterCache): ResponseAdapter<Data> {
    val adapter = customScalarAdapters.getOperationAdapter(name()) {
      TestQuery_ResponseAdapter(customScalarAdapters)
    }
    return adapter
  }

  override fun responseFields(): List<ResponseField.FieldSet> = listOf(
    ResponseField.FieldSet(null, TestQuery_ResponseAdapter.RESPONSE_FIELDS)
  )
  /**
   * The query type, represents all of the entry points into our object graph
   */
  data class Data(
    val hero: Hero?
  ) : Operation.Data {
    /**
     * A character from the Star Wars universe
     */
    data class Hero(
      /**
       * The name of the character
       */
      val name: String,
      /**
       * Test deprecated field
       */
      @Deprecated(message = "For test purpose only")
      val deprecated: String,
      /**
       * Test deprecated field
       */
      @Deprecated(message = "For test purpose only")
      val deprecatedBool: Boolean
    )
  }

  companion object {
    const val OPERATION_ID: String =
        "6a8dd101c1681dc829d72040868fdf394c52bacb692da3549bc73e9504013470"

    val QUERY_DOCUMENT: String = QueryDocumentMinifier.minify(
          """
          |query TestQuery(${'$'}episode: Episode) {
          |  hero(episode: ${'$'}episode) {
          |    name
          |    deprecated
          |    deprecatedBool
          |  }
          |}
          """.trimMargin()
        )

    val OPERATION_NAME: String = "TestQuery"
  }
}
