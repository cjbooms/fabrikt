package examples.openapi310OptionalObject.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class NullableObject(
  @param:JsonProperty("field")
  @get:JsonProperty("field")
  @get:Valid
  public val `field`: TheObject? = null,
)
