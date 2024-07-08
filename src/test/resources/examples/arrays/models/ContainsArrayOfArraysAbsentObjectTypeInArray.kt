package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ContainsArrayOfArraysAbsentObjectTypeInArray(
  @param:JsonProperty("some-array-prop")
  @get:JsonProperty("some-array-prop")
  public val someArrayProp: String? = null,
)
