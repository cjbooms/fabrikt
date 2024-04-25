package examples.enumExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class FooBarsBar(
  @param:JsonProperty("bar_prop")
  @get:JsonProperty("bar_prop")
  public val barProp: String? = null,
)
