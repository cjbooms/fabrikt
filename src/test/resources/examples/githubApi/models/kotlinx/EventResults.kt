package examples.githubApi.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class EventResults(
  @SerialName("change_events")
  @get:NotNull
  @get:Size(min = 0)
  @get:Valid
  public val changeEvents: List<Event>,
)
