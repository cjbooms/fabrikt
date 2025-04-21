package examples.openapi310.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Any

public data class NewNullableFormatComplexNullable(
  @param:JsonProperty("oneOf")
  @get:JsonProperty("oneOf")
  public val oneOf: Any? = null,
)
