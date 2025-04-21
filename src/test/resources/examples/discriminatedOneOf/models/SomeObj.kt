package examples.discriminatedOneOf.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.collections.List

public data class SomeObj(
  @param:JsonProperty("state")
  @get:JsonProperty("state")
  @get:NotNull
  @get:Valid
  public val state: State,
  @param:JsonProperty("inlinedArray")
  @get:JsonProperty("inlinedArray")
  @get:Valid
  public val inlinedArray: List<SomeObjInlinedArray>? = null,
  @param:JsonProperty("inlinedObject")
  @get:JsonProperty("inlinedObject")
  @get:Valid
  public val inlinedObject: SomeObjInlinedObject? = null,
)
