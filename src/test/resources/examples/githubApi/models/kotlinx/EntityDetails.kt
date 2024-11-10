package examples.githubApi.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class EntityDetails(
  @SerialName("id")
  @get:NotNull
  public val id: String,
)
