package examples.githubApi.models

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Author(
  @SerialName("name")
  public val name: String? = null,
  @SerialName("email")
  public val email: String? = null,
)
