// AUTO-GENERATED FILE. DO NOT MODIFY.
//
// This class was automatically generated by Apollo GraphQL plugin from the GraphQL queries it found.
// It should not be modified by hand.
//
package com.example.simple_fragment.fragment

import com.apollographql.apollo.api.GraphqlFragment
import com.apollographql.apollo.api.internal.ResponseAdapter
import com.example.simple_fragment.fragment.adapter.HeroDetailsImpl_ResponseAdapter
import kotlin.String

/**
 * A character from the Star Wars universe
 */
internal interface HeroDetailsImpl : HeroDetail, GraphqlFragment, Adaptable<HeroDetailsImpl> {
  override val __typename: String

  override fun adapter(): ResponseAdapter<HeroDetailsImpl> {
    return HeroDetailsImpl_ResponseAdapter
  }

  data class HumanHeroDetailsImpl(
    override val __typename: String,
    /**
     * What this human calls themselves
     */
    override val name: String
  ) : HeroDetail, HeroDetail.Human, HumanDetail, HeroDetailsImpl

  data class OtherHeroDetailsImpl(
    override val __typename: String
  ) : HeroDetail, HeroDetailsImpl
}
