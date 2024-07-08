package examples.nullableArrayItems.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.collections.List

public data class NullableArrayItems(
  @param:JsonProperty("anArray")
  @get:JsonProperty("anArray")
  public val anArray: List<String?>?,
)
