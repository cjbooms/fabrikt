package examples.externalReferences.models

import com.fasterxml.jackson.`annotation`.JsonAnyGetter
import com.fasterxml.jackson.`annotation`.JsonAnySetter
import com.fasterxml.jackson.`annotation`.JsonIgnore
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap

public data class Answer(
    @param:JsonProperty("content")
    @get:JsonProperty("content")
    @get:NotNull
    @get:Valid
    public val content: Content,
)

public data class ContainingExternalReference(
    @param:JsonProperty("some-external-reference")
    @get:JsonProperty("some-external-reference")
    @get:Valid
    public val someExternalReference: ExternalObject? = null,
)

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "contentType",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ContentHTML::class, name = "HTML"),
    JsonSubTypes.Type(
        value =
        ContentMD::class,
        name = "MD",
    ),
)
public sealed class Content() {
    public abstract val contentType: ContentContentType
}

public enum class ContentContentType(
    @JsonValue
    public val `value`: String,
) {
    HTML("HTML"),
    MD("MD"),
    ;

    public companion object {
        private val mapping: Map<String, ContentContentType> =
            values().associateBy(ContentContentType::value)

        public fun fromValue(`value`: String): ContentContentType? = mapping[value]
    }
}

public data class ContentHTML(
    @param:JsonProperty("header")
    @get:JsonProperty("header")
    public val `header`: String? = null,
    @param:JsonProperty("body")
    @get:JsonProperty("body")
    public val body: String? = null,
    @get:JsonProperty("contentType")
    @get:NotNull
    @param:JsonProperty("contentType")
    override val contentType: ContentContentType = ContentContentType.HTML,
) : Content()

public data class ContentMD(
    @param:JsonProperty("richText")
    @get:JsonProperty("richText")
    public val richText: String? = null,
    @get:JsonProperty("contentType")
    @get:NotNull
    @param:JsonProperty("contentType")
    override val contentType: ContentContentType = ContentContentType.MD,
) : Content()

public data class ExternalObject(
    @param:JsonProperty("another")
    @get:JsonProperty("another")
    @get:Valid
    public val another: ExternalObjectTwo? = null,
    @param:JsonProperty("one_of")
    @get:JsonProperty("one_of")
    @get:Valid
    public val oneOf: ParentOneOf? = null,
)

public data class ExternalObjectFour(
    @param:JsonProperty("blah")
    @get:JsonProperty("blah")
    public val blah: String? = null,
)

public data class ExternalObjectThree(
    @param:JsonProperty("enum")
    @get:JsonProperty("enum")
    @get:NotNull
    public val `enum`: ExternalObjectThreeEnum,
    @param:JsonProperty("description")
    @get:JsonProperty("description")
    @get:NotNull
    public val description: String,
)

public enum class ExternalObjectThreeEnum(
    @JsonValue
    public val `value`: String,
) {
    ONE("one"),
    TWO("two"),
    THREE("three"),
    ;

    public companion object {
        private val mapping: Map<String, ExternalObjectThreeEnum> =
            values().associateBy(ExternalObjectThreeEnum::value)

        public fun fromValue(`value`: String): ExternalObjectThreeEnum? = mapping[value]
    }
}

public data class ExternalObjectTwo(
    @param:JsonProperty("list-others")
    @get:JsonProperty("list-others")
    @get:Valid
    public val listOthers: List<ExternalObjectThree>? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, Map<String, ExternalObjectFour>> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, Map<String, ExternalObjectFour>> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Map<String, ExternalObjectFour>) {
        properties[name] = value
    }
}

public data class IgnoredExternalObjectFive(
    @param:JsonProperty("blah")
    @get:JsonProperty("blah")
    public val blah: String? = null,
)

public enum class Language(
    @JsonValue
    public val `value`: String,
) {
    ENGLISH("ENGLISH"),
    DUTCH("DUTCH"),
    GERMAN("GERMAN"),
    ;

    public companion object {
        private val mapping: Map<String, Language> = values().associateBy(Language::value)

        public fun fromValue(`value`: String): Language? = mapping[value]
    }
}

public data class OneOfOne(
    @param:JsonProperty("oneOfOne")
    @get:JsonProperty("oneOfOne")
    public val oneOfOne: String? = null,
    @get:JsonProperty("discriminator")
    @get:NotNull
    @param:JsonProperty("discriminator")
    override val discriminator: String = "OneOfOne",
) : ParentOneOf()

public data class OneOfTwo(
    @param:JsonProperty("oneOfTwo")
    @get:JsonProperty("oneOfTwo")
    public val oneOfTwo: String? = null,
    @get:JsonProperty("discriminator")
    @get:NotNull
    @param:JsonProperty("discriminator")
    override val discriminator: String = "OneOfTwo",
) : ParentOneOf()

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "discriminator",
    visible = true,
)
@JsonSubTypes(
    JsonSubTypes.Type(value = OneOfOne::class, name = "OneOfOne"),
    JsonSubTypes.Type(
        value =
        OneOfTwo::class,
        name = "OneOfTwo",
    ),
)
public sealed class ParentOneOf() {
    public abstract val discriminator: String
}
