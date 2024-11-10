package examples.githubApi.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PullRequestQueryResult(
  @SerialName("prev")
  public val prev: String? = null,
  @SerialName("next")
  public val next: String? = null,
  @SerialName("items")
  @get:NotNull
  @get:Size(min = 0)
  @get:Valid
  public val items: List<PullRequest>,
)
