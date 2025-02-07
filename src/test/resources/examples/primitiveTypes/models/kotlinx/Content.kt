package examples.primitiveTypes.models

import java.math.BigDecimal
import kotlin.Boolean
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Content(
  @SerialName("integer")
  public val integer: Int? = null,
  @SerialName("integer32")
  public val integer32: Int? = null,
  @SerialName("integer64")
  public val integer64: Long? = null,
  @SerialName("boolean")
  public val boolean: Boolean? = null,
  @SerialName("string")
  public val string: String? = null,
  @SerialName("stringUuid")
  public val stringUuid: String? = null,
  @SerialName("stringUri")
  public val stringUri: String? = null,
  @SerialName("stringDate")
  public val stringDate: LocalDate? = null,
  @SerialName("stringDateTime")
  public val stringDateTime: Instant? = null,
  @Contextual
  @SerialName("number")
  public val number: BigDecimal? = null,
  @SerialName("numberFloat")
  public val numberFloat: Float? = null,
  @SerialName("numberDouble")
  public val numberDouble: Double? = null,
  @SerialName("base64")
  public val base64: String? = null,
  @SerialName("binary")
  public val binary: String? = null,
)
