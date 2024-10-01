package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class BasicObjectDto(
  @param:JsonProperty("one")
  @get:JsonProperty("one")
  public val one: String? = null,
)
