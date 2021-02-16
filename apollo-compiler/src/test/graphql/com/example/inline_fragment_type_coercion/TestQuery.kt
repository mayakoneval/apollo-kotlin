// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.inline_fragment_type_coercion

import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.Query
import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.QueryDocumentMinifier
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.inline_fragment_type_coercion.adapter.TestQuery_ResponseAdapter
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
class TestQuery : Query<TestQuery.Data> {
  override fun operationId(): String = OPERATION_ID

  override fun queryDocument(): String = QUERY_DOCUMENT

  override fun variables(): Operation.Variables = Operation.EMPTY_VARIABLES

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
    /**
     * For testing fragment type coercion
     */
    val foo: Foo?
  ) : Operation.Data {
    /**
     * For testing fragment type coercion
     */
    interface Foo {
      val __typename: String

      val foo: String

      data class BarFoo(
        override val __typename: String,
        override val foo: String,
        val bar: String
      ) : Foo

      data class OtherFoo(
        override val __typename: String,
        override val foo: String
      ) : Foo

      companion object {
        fun Foo.asBarFoo(): BarFoo? = this as? BarFoo
      }
    }
  }

  companion object {
    const val OPERATION_ID: String =
        "65c4fd857f5cbd2283f0783a3b3cefd9ead5abb159f5cc20922b0d8e04286662"

    val QUERY_DOCUMENT: String = QueryDocumentMinifier.minify(
          """
          |query TestQuery {
          |  foo {
          |    __typename
          |    foo
          |    ... on Bar {
          |      bar
          |    }
          |  }
          |}
          """.trimMargin()
        )

    val OPERATION_NAME: String = "TestQuery"
  }
}
