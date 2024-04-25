package examples.instantDateTime.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.Instant
import kotlin.collections.List

public data class FirstModel(
  @param:JsonProperty("date")
  @get:JsonProperty("date")
  public val date: Instant? = null,
  @param:JsonProperty("extra_first_attr")
  @get:JsonProperty("extra_first_attr")
  public val extraFirstAttr: List<Instant>? = null,
)
