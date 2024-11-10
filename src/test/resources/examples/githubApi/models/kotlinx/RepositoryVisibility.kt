package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class RepositoryVisibility(
  public val `value`: String,
) {
  @SerialName("Private")
  PRIVATE("Private"),
  @SerialName("Public")
  PUBLIC("Public"),
  ;

  public companion object {
    private val mapping: Map<String, RepositoryVisibility> =
        values().associateBy(RepositoryVisibility::value)

    public fun fromValue(`value`: String): RepositoryVisibility? = mapping[value]
  }
}
