package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class PullRequestStatus(
  public val `value`: String,
) {
  @SerialName("active")
  ACTIVE("active"),
  @SerialName("inactive")
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, PullRequestStatus> =
        values().associateBy(PullRequestStatus::value)

    public fun fromValue(`value`: String): PullRequestStatus? = mapping[value]
  }
}
