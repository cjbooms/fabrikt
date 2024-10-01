package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ExternalObjectFourDto(
  @param:JsonProperty("blah")
  @get:JsonProperty("blah")
  public val blah: String? = null,
)
