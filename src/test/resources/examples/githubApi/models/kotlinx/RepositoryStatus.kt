package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class RepositoryStatus(
  public val `value`: String,
) {
  @SerialName("active")
  ACTIVE("active"),
  @SerialName("inactive")
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, RepositoryStatus> =
        values().associateBy(RepositoryStatus::value)

    public fun fromValue(`value`: String): RepositoryStatus? = mapping[value]
  }
}
