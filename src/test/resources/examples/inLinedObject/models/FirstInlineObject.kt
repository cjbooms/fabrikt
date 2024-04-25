package examples.inLinedObject.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class FirstInlineObject(
  @param:JsonProperty("generation")
  @get:JsonProperty("generation")
  @get:Valid
  public val generation: FirstInlineObjectGeneration? = null,
)
