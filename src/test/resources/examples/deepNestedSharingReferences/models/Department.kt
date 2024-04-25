package examples.deepNestedSharingReferences.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class Department(
  @param:JsonProperty("supervisor")
  @get:JsonProperty("supervisor")
  @get:Valid
  public val supervisor: Person? = null,
  @param:JsonProperty("manager")
  @get:JsonProperty("manager")
  @get:Valid
  public val manager: Person? = null,
)
