package examples.githubApi.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class RepositoryVisibility(
  @JsonValue
  public val `value`: String,
) {
  PRIVATE("Private"),
  PUBLIC("Public"),
  ;

  public companion object {
    private val mapping: Map<String, RepositoryVisibility> =
        values().associateBy(RepositoryVisibility::value)

    public fun fromValue(`value`: String): RepositoryVisibility? = mapping[value]
  }
}
