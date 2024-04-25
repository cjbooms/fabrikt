package examples.mapExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

public data class InlinedTypedObjectMapValue(
  @param:JsonProperty("code")
  @get:JsonProperty("code")
  public val code: Int? = null,
  @param:JsonProperty("text")
  @get:JsonProperty("text")
  public val text: String? = null,
)
