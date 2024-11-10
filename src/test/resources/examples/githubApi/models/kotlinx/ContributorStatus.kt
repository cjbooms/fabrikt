package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class ContributorStatus(
  public val `value`: String,
) {
  @SerialName("active")
  ACTIVE("active"),
  @SerialName("inactive")
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, ContributorStatus> =
        values().associateBy(ContributorStatus::value)

    public fun fromValue(`value`: String): ContributorStatus? = mapping[value]
  }
}
