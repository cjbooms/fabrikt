package examples.githubApi.models

import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Event(
  @SerialName("entity_id")
  @get:NotNull
  public val entityId: String,
  @SerialName("data")
  @get:NotNull
  public val `data`: Map<String, Any?>,
)
