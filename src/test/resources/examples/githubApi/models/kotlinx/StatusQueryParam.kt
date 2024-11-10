package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class StatusQueryParam(
  public val `value`: String,
) {
  @SerialName("active")
  ACTIVE("active"),
  @SerialName("inactive")
  INACTIVE("inactive"),
  @SerialName("all")
  ALL("all"),
  ;

  public companion object {
    private val mapping: Map<String, StatusQueryParam> =
        values().associateBy(StatusQueryParam::value)

    public fun fromValue(`value`: String): StatusQueryParam? = mapping[value]
  }
}
