package examples.inlinedAggregatedObjects.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class DogWalker(
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  @get:NotNull
  public val name: String,
  @param:JsonProperty("companyName")
  @get:JsonProperty("companyName")
  @get:NotNull
  public val companyName: String,
)
