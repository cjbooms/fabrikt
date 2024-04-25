package examples.githubApi.multipleFiles.models

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
        values().associateBy(RepositoryStatus::value)

    public fun fromValue(`value`: String): RepositoryStatus? = mapping[value]
  }
}
