// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.input_object_type.adapter

import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.IntResponseAdapter
import com.apollographql.apollo.api.internal.NullableResponseAdapter
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.apollographql.apollo.api.internal.StringResponseAdapter
import com.apollographql.apollo.api.internal.json.JsonReader
import com.apollographql.apollo.api.internal.json.JsonWriter
import com.example.input_object_type.TestQuery
import kotlin.Array
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Suppress("NAME_SHADOWING", "UNUSED_ANONYMOUS_PARAMETER", "LocalVariableName",
    "RemoveExplicitTypeArguments", "NestedLambdaShadowedImplicitParameter", "PropertyName",
    "RemoveRedundantQualifierName")
class TestQuery_ResponseAdapter(
  responseAdapterCache: ResponseAdapterCache
) : ResponseAdapter<TestQuery.Data> {
  private val nullableCreateReviewAdapter: ResponseAdapter<TestQuery.Data.CreateReview?> =
      NullableResponseAdapter(CreateReview(responseAdapterCache))

  override fun fromResponse(reader: JsonReader): TestQuery.Data {
    var createReview: TestQuery.Data.CreateReview? = null
    reader.beginObject()
    while(true) {
      when (reader.selectName(RESPONSE_NAMES)) {
        0 -> createReview = nullableCreateReviewAdapter.fromResponse(reader)
        else -> break
      }
    }
    reader.endObject()
    return TestQuery.Data(
      createReview = createReview
    )
  }

  override fun toResponse(writer: JsonWriter, value: TestQuery.Data) {
    writer.beginObject()
    writer.name("createReview")
    nullableCreateReviewAdapter.toResponse(writer, value.createReview)
    writer.endObject()
  }

  companion object {
    val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
      ResponseField(
        type = ResponseField.Type.Named.Object("Review"),
        fieldName = "createReview",
        arguments = mapOf<String, Any?>(
          "episode" to mapOf<String, Any?>(
            "kind" to "Variable",
            "variableName" to "ep"),
          "review" to mapOf<String, Any?>(
            "kind" to "Variable",
            "variableName" to "review")),
        fieldSets = listOf(
          ResponseField.FieldSet(null, CreateReview.RESPONSE_FIELDS)
        ),
      )
    )

    val RESPONSE_NAMES: List<String> = RESPONSE_FIELDS.map { it.responseName }
  }

  class CreateReview(
    responseAdapterCache: ResponseAdapterCache
  ) : ResponseAdapter<TestQuery.Data.CreateReview> {
    private val intAdapter: ResponseAdapter<Int> = IntResponseAdapter

    private val nullableStringAdapter: ResponseAdapter<String?> =
        NullableResponseAdapter(StringResponseAdapter)

    override fun fromResponse(reader: JsonReader): TestQuery.Data.CreateReview {
      var stars: Int? = null
      var commentary: String? = null
      reader.beginObject()
      while(true) {
        when (reader.selectName(RESPONSE_NAMES)) {
          0 -> stars = intAdapter.fromResponse(reader)
          1 -> commentary = nullableStringAdapter.fromResponse(reader)
          else -> break
        }
      }
      reader.endObject()
      return TestQuery.Data.CreateReview(
        stars = stars!!,
        commentary = commentary
      )
    }

    override fun toResponse(writer: JsonWriter, value: TestQuery.Data.CreateReview) {
      writer.beginObject()
      writer.name("stars")
      intAdapter.toResponse(writer, value.stars)
      writer.name("commentary")
      nullableStringAdapter.toResponse(writer, value.commentary)
      writer.endObject()
    }

    companion object {
      val RESPONSE_FIELDS: Array<ResponseField> = arrayOf(
        ResponseField(
          type = ResponseField.Type.NotNull(ResponseField.Type.Named.Other("Int")),
          fieldName = "stars",
        ),
        ResponseField(
          type = ResponseField.Type.Named.Other("String"),
          fieldName = "commentary",
        )
      )

      val RESPONSE_NAMES: List<String> = RESPONSE_FIELDS.map { it.responseName }
    }
  }
}
