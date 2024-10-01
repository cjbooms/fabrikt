package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class BarBarDto(
  @param:JsonProperty("bar_prop")
  @get:JsonProperty("bar_prop")
  public val barProp: String? = null,
)
