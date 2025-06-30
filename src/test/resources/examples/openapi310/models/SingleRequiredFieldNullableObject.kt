package examples.openapi310.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class SingleRequiredFieldNullableObject(
  @param:JsonProperty("requiredNullableRef")
  @get:JsonProperty("requiredNullableRef")
  @get:Valid
  public val requiredNullableRef: OneObject? = null,
)
