package examples.discriminatedOneOf.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SomeObj(
  @SerialName("state")
  @get:NotNull
  @get:Valid
  public val state: State,
  @SerialName("inlinedArray")
  @get:Valid
  public val inlinedArray: List<SomeObjInlinedArray>? = null,
  @SerialName("inlinedObject")
  @get:Valid
  public val inlinedObject: SomeObjInlinedObject? = null,
)
