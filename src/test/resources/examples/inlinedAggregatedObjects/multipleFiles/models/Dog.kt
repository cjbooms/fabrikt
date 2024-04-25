package examples.inlinedAggregatedObjects.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class Dog(
  @param:JsonProperty("owner")
  @get:JsonProperty("owner")
  @get:Valid
  public val owner: Person? = null,
  @param:JsonProperty("walker")
  @get:JsonProperty("walker")
  @get:Valid
  public val walker: DogWalker? = null,
)
