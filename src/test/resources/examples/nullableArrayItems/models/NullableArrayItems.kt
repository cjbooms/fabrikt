package examples.nullableArrayItems.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

public data class NullableArrayItems(
  @param:JsonProperty("anArray")
  @get:JsonProperty("anArray")
  @get:NotNull
  public val anArray: List<String?>,
)
