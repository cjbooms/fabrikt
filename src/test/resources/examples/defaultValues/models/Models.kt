package examples.defaultValues.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonValue
import java.net.URI
import java.util.Base64
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.ByteArray
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

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

public enum class PersonWithDefaultsEnumDefault(
    @JsonValue
    public val `value`: String,
) {
    TALL("tall"),
    SHORT("short"),
    ;

    public companion object {
        private val mapping: Map<String, PersonWithDefaultsEnumDefault> =
            values().associateBy(PersonWithDefaultsEnumDefault::value)

        public fun fromValue(`value`: String): PersonWithDefaultsEnumDefault? = mapping[value]
    }
}
