package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class Author(
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  public val name: String? = null,
  @param:JsonProperty("email")
  @get:JsonProperty("email")
  public val email: String? = null,
)
