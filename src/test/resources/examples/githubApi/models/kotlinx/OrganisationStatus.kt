package examples.githubApi.models

import kotlin.String
import kotlin.collections.Map
import kotlinx.serialization.SerialName

public enum class OrganisationStatus(
  public val `value`: String,
) {
  @SerialName("active")
  ACTIVE("active"),
  @SerialName("inactive")
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, OrganisationStatus> =
        values().associateBy(OrganisationStatus::value)

    public fun fromValue(`value`: String): OrganisationStatus? = mapping[value]
  }
}
