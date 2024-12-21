package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull

internal class StateB(
  @get:JsonProperty("mode")
  @get:NotNull
  internal val mode: StateBMode,
  @get:JsonProperty("status")
  @get:NotNull
  @param:JsonProperty("status")
  internal val status: Status = Status.B,
) : State
