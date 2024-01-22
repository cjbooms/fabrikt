package examples.externalReferences.targeted.models

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

public data class ContainingExternalReference(
    @param:JsonProperty("some-external-reference")
    @get:JsonProperty("some-external-reference")
    @get:Valid
    public val someExternalReference: ExternalObject? = null,
)

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
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Map<String, ExternalObjectFour>> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Map<String, ExternalObjectFour>> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Map<String, ExternalObjectFour>) {
        properties[name] = value
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
