package examples.BugJens1.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.collections.List

public data class `200Result`(
  @param:JsonProperty("results")
  @get:JsonProperty("results")
  @get:Valid
  public val results: List<`200ResultResults`>? = null,
)
