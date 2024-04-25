package examples.oneOfMarkerInterface.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Any

public data class Container(
  @param:JsonProperty("state")
  @get:JsonProperty("state")
  public val state: Any? = null,
)
