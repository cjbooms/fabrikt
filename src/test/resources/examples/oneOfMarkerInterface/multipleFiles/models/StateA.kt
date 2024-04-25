package examples.oneOfMarkerInterface.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class StateA(
  @get:JsonProperty("status")
  @get:NotNull
  public val status: String,
) : State
