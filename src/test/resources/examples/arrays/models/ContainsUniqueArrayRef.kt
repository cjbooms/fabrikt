package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.collections.Set

public data class ContainsUniqueArrayRef(
  @param:JsonProperty("weight_on_mars")
  @get:JsonProperty("weight_on_mars")
  @get:NotNull
  @get:Valid
  public val weightOnMars: Set<ArrayRef>,
)
