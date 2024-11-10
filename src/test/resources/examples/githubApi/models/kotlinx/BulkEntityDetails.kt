package examples.githubApi.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class BulkEntityDetails(
  @SerialName("entities")
  @get:NotNull
  @get:Valid
  public val entities: List<EntityDetails>,
)
