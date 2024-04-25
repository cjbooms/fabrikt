package examples.mapExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class BasicObject(
  @param:JsonProperty("one")
  @get:JsonProperty("one")
  public val one: String? = null,
)
