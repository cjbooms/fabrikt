package examples.discriminatedOneOf.models

import javax.validation.constraints.NotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("a")
@Serializable
public data class StateA(
  @SerialName("status")
  @get:NotNull
  public val status: Status = Status.A,
) : State
