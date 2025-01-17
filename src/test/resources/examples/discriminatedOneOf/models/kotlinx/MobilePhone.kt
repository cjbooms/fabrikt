package examples.discriminatedOneOf.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("mobile")
@Serializable
public data class MobilePhone(
  @SerialName("number")
  @get:NotNull
  public val number: String,
) : Phone
