package examples.oneOfMarkerInterface.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class StateB(
  @get:JsonProperty("mode")
  @get:NotNull
  public val mode: String,
) : State
