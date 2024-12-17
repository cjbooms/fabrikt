package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class RepositoryStatus(
  @JsonValue
  public val `value`: String,
) {
  ACTIVE("active"),
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, RepositoryStatus> =
        entries.associateBy(RepositoryStatus::value)

    public fun fromValue(`value`: String): RepositoryStatus? = mapping[value]
  }
}
