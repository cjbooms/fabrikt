package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

internal class SomeObj(
  @param:JsonProperty("state")
  @get:JsonProperty("state")
  @get:NotNull
  @get:Valid
  internal val state: State,
)
