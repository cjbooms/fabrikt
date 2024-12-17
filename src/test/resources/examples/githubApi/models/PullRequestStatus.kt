package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class PullRequestStatus(
  @JsonValue
  public val `value`: String,
) {
  ACTIVE("active"),
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, PullRequestStatus> =
        entries.associateBy(PullRequestStatus::value)

    public fun fromValue(`value`: String): PullRequestStatus? = mapping[value]
  }
}
