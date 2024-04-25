package examples.arrays.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int

public data class ArrayRef(
  @param:JsonProperty("grams")
  @get:JsonProperty("grams")
  public val grams: Int? = null,
)
