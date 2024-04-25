package examples.deepNestedSharingReferences.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class Person(
  @param:JsonProperty("home_address")
  @get:JsonProperty("home_address")
  @get:Valid
  public val homeAddress: Address? = null,
)
