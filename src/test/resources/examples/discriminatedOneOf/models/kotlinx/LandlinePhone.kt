package examples.discriminatedOneOf.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@SerialName("landline")
@Serializable
public data class LandlinePhone(
  @SerialName("number")
  @get:NotNull
  public val number: String,
  @SerialName("area_code")
  @get:NotNull
  public val areaCode: String,
) : Phone
