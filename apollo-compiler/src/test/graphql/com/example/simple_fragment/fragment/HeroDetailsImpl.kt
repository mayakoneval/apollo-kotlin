// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.simple_fragment.fragment

import com.apollographql.apollo.api.Fragment
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.simple_fragment.fragment.adapter.HeroDetailsImpl_ResponseAdapter
import kotlin.String
import kotlin.collections.List

internal class HeroDetailsImpl : Fragment<HeroDetailsImpl.Data> {
  override fun adapter(customScalarAdapters: ResponseAdapterCache): ResponseAdapter<Data> {
    val adapter = customScalarAdapters.getFragmentAdapter("HeroDetailsImpl") {
      HeroDetailsImpl_ResponseAdapter(customScalarAdapters)
    }
    return adapter
  }

  override fun responseFields(): List<ResponseField.FieldSet> = listOf(
    ResponseField.FieldSet("Human", HeroDetailsImpl_ResponseAdapter.HumanData.RESPONSE_FIELDS),
    ResponseField.FieldSet(null, HeroDetailsImpl_ResponseAdapter.OtherData.RESPONSE_FIELDS),
  )
  override fun variables(): Operation.Variables = Operation.EMPTY_VARIABLES

  /**
   * A character from the Star Wars universe
   */
  interface Data : HeroDetails, Fragment.Data {
    data class HumanData(
      override val __typename: String,
      /**
       * What this human calls themselves
       */
      override val name: String
    ) : HeroDetails, HeroDetails.Human, HumanDetails, Data

    data class OtherData(
      override val __typename: String
    ) : HeroDetails, Data
  }
}
