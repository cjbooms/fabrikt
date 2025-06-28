package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

/**
 * States whether the entity is currently active or inactive
 */
public enum class OrganisationStatus(
  @JsonValue
  public val `value`: String,
) {
  ACTIVE("active"),
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, OrganisationStatus> =
        entries.associateBy(OrganisationStatus::value)

    public fun fromValue(`value`: String): OrganisationStatus? = mapping[value]
  }
}
