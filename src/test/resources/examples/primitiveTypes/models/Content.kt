package examples.primitiveTypes.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String

public data class Content(
  @param:JsonProperty("integer")
  @get:JsonProperty("integer")
  public val integer: Int? = null,
  @param:JsonProperty("integer32")
  @get:JsonProperty("integer32")
  public val integer32: Int? = null,
  @param:JsonProperty("integer64")
  @get:JsonProperty("integer64")
  public val integer64: Long? = null,
  @param:JsonProperty("boolean")
  @get:JsonProperty("boolean")
  public val boolean: Boolean? = null,
  @param:JsonProperty("string")
  @get:JsonProperty("string")
  public val string: String? = null,
  @param:JsonProperty("stringUuid")
  @get:JsonProperty("stringUuid")
  public val stringUuid: UUID? = null,
  @param:JsonProperty("stringUri")
  @get:JsonProperty("stringUri")
  public val stringUri: URI? = null,
  @param:JsonProperty("stringDate")
  @get:JsonProperty("stringDate")
  public val stringDate: LocalDate? = null,
  @param:JsonProperty("stringDateTime")
  @get:JsonProperty("stringDateTime")
  public val stringDateTime: OffsetDateTime? = null,
  @param:JsonProperty("number")
  @get:JsonProperty("number")
  public val number: BigDecimal? = null,
  @param:JsonProperty("numberFloat")
  @get:JsonProperty("numberFloat")
  public val numberFloat: Float? = null,
  @param:JsonProperty("numberDouble")
  @get:JsonProperty("numberDouble")
  public val numberDouble: Double? = null,
  @param:JsonProperty("byte")
  @get:JsonProperty("byte")
  public val byte: ByteArray? = null,
  @param:JsonProperty("binary")
  @get:JsonProperty("binary")
  public val binary: ByteArray? = null,
)
