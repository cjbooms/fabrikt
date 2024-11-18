package examples.discriminatedOneOf.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SomeObj(
  @SerialName("state")
  @get:NotNull
  @get:Valid
  public val state: State,
)
