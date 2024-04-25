package examples.mapExamples.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

public data class TypedObjectMapValue(
  @param:JsonProperty("code")
  @get:JsonProperty("code")
  public val code: Int? = null,
  @param:JsonProperty("text")
  @get:JsonProperty("text")
  public val text: String? = null,
)
