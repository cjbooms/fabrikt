package examples.githubApi.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.List

public data class PullRequestQueryResult(
  @param:JsonProperty("prev")
  @get:JsonProperty("prev")
  public val prev: URI? = null,
  @param:JsonProperty("next")
  @get:JsonProperty("next")
  public val next: URI? = null,
  @param:JsonProperty("items")
  @get:JsonProperty("items")
  @get:NotNull
  @get:Size(min = 0)
  @get:Valid
  public val items: List<PullRequest>,
)
