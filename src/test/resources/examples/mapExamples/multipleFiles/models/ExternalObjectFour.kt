package examples.mapExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ExternalObjectFour(
  @param:JsonProperty("blah")
  @get:JsonProperty("blah")
  public val blah: String? = null,
)
