package examples.defaultValues.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.net.URI
import java.util.Base64
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Int
import kotlin.String

public data class PersonWithDefaults(
  @param:JsonProperty("required_so_default_ignored")
  @get:JsonProperty("required_so_default_ignored")
  @get:NotNull
  public val requiredSoDefaultIgnored: String,
  @param:JsonProperty("integer_default")
  @get:JsonProperty("integer_default")
  @get:NotNull
  public val integerDefault: Int = 18,
  @param:JsonProperty("enum_default")
  @get:JsonProperty("enum_default")
  @get:NotNull
  public val enumDefault: PersonWithDefaultsEnumDefault = PersonWithDefaultsEnumDefault.TALL,
  @param:JsonProperty("enum_quoted_default")
  @get:JsonProperty("enum_quoted_default")
  @get:NotNull
  public val enumQuotedDefault: PersonWithDefaultsEnumQuotedDefault =
      PersonWithDefaultsEnumQuotedDefault.`2X`,
  @param:JsonProperty("boolean_default")
  @get:JsonProperty("boolean_default")
  @get:NotNull
  public val booleanDefault: Boolean = true,
  @param:JsonProperty("string_phrase")
  @get:JsonProperty("string_phrase")
  @get:NotNull
  public val stringPhrase: String = "Cowabunga Dude",
  @param:JsonProperty("uri_type")
  @get:JsonProperty("uri_type")
  @get:NotNull
  public val uriType: URI = URI("about:blank"),
  @param:JsonProperty("byte_type")
  @get:JsonProperty("byte_type")
  @get:NotNull
  public val byteType: ByteArray = Base64.getDecoder().decode("U3dhZ2dlciByb2Nrcw=="),
)
