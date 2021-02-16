// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.named_fragment_inside_inline_fragment.fragment

import com.apollographql.apollo.api.Fragment
import com.apollographql.apollo.api.Operation
import com.apollographql.apollo.api.ResponseAdapterCache
import com.apollographql.apollo.api.ResponseField
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.named_fragment_inside_inline_fragment.fragment.adapter.CharacterNameImpl_ResponseAdapter
import kotlin.String
import kotlin.collections.List

class CharacterNameImpl : Fragment<CharacterNameImpl.Data> {
  override fun adapter(customScalarAdapters: ResponseAdapterCache): ResponseAdapter<Data> {
    val adapter = customScalarAdapters.getFragmentAdapter("CharacterNameImpl") {
      CharacterNameImpl_ResponseAdapter(customScalarAdapters)
    }
    return adapter
  }

  override fun responseFields(): List<ResponseField.FieldSet> = listOf(
    ResponseField.FieldSet(null, CharacterNameImpl_ResponseAdapter.RESPONSE_FIELDS)
  )
  override fun variables(): Operation.Variables = Operation.EMPTY_VARIABLES

  /**
   * A character from the Star Wars universe
   */
  data class Data(
    override val __typename: String = "Character",
    /**
     * The name of the character
     */
    override val name: String
  ) : CharacterName, Fragment.Data
}
