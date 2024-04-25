package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.collections.List

public data class ContainsArrayOfArrays(
  @param:JsonProperty("array_of_arrays")
  @get:JsonProperty("array_of_arrays")
  @get:Valid
  public val arrayOfArrays: List<List<Something>>? = null,
)
