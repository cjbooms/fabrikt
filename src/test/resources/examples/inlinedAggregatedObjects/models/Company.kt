package examples.inlinedAggregatedObjects.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class Company(
  @param:JsonProperty("companyName")
  @get:JsonProperty("companyName")
  @get:NotNull
  public val companyName: String,
)
