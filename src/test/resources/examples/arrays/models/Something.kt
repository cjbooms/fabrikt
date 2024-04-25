package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int

public data class Something(
  @param:JsonProperty("some_value")
  @get:JsonProperty("some_value")
  public val someValue: Int? = null,
)
