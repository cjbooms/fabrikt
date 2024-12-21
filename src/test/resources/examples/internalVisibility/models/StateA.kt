package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull

internal class StateA(
  @get:JsonProperty("status")
  @get:NotNull
  @param:JsonProperty("status")
  internal val status: Status = Status.A,
) : State
