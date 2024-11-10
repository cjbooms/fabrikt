package examples.githubApi.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Webhook(
  @SerialName("url")
  @get:NotNull
  public val url: String,
  @SerialName("name")
  public val name: String? = null,
)
