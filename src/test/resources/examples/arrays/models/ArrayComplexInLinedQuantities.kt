package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ArrayComplexInLinedQuantities(
  @param:JsonProperty("prop_one")
  @get:JsonProperty("prop_one")
  public val propOne: String? = null,
)
