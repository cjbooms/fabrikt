package examples.externalReferences.models

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap

data class ContainingExternalReference(
    @param:JsonProperty("some-external-reference")
    @get:JsonProperty("some-external-reference")
    @get:Valid
    val someExternalReference: ExternalObject? = null
)

data class ExternalObject(
    @param:JsonProperty("another")
    @get:JsonProperty("another")
    @get:Valid
    val another: ExternalObjectTwo? = null,
    @param:JsonProperty("one_of")
    @get:JsonProperty("one_of")
    @get:Valid
    val oneOf: ParentOneOf? = null
)

data class ExternalObjectFour(
    @param:JsonProperty("blah")
    @get:JsonProperty("blah")
    val blah: String? = null
)

data class ExternalObjectThree(
    @param:JsonProperty("enum")
    @get:JsonProperty("enum")
    @get:NotNull
    val enum: ExternalObjectThreeEnum,
    @param:JsonProperty("description")
    @get:JsonProperty("description")
    @get:NotNull
    val description: String
)

enum class ExternalObjectThreeEnum(
    @JsonValue
    val value: String
) {
    ONE("one"),

    TWO("two"),

    THREE("three");

    companion object {
        private val mapping: Map<String, ExternalObjectThreeEnum> =
            values().associateBy(ExternalObjectThreeEnum::value)

        fun fromValue(value: String): ExternalObjectThreeEnum? = mapping[value]
    }
}

data class ExternalObjectTwo(
    @param:JsonProperty("list-others")
    @get:JsonProperty("list-others")
    @get:Valid
    val listOthers: List<ExternalObjectThree>? = null
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Map<String, ExternalObjectFour>> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Map<String, ExternalObjectFour>> = properties

    @JsonAnySetter
    fun set(name: String, value: Map<String, ExternalObjectFour>) {
        properties[name] = value
    }
}

data class OneOfOne(
    @param:JsonProperty("oneOfOne")
    @get:JsonProperty("oneOfOne")
    val oneOfOne: String? = null
) : ParentOneOf() {
    @get:JsonProperty("discriminator")
    @get:NotNull
    override val discriminator: String = "OneOfOne"
}

data class OneOfTwo(
    @param:JsonProperty("oneOfTwo")
    @get:JsonProperty("oneOfTwo")
    val oneOfTwo: String? = null
) : ParentOneOf() {
    @get:JsonProperty("discriminator")
    @get:NotNull
    override val discriminator: String = "OneOfTwo"
}

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "discriminator",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = OneOfOne::class, name = "OneOfOne"),
    JsonSubTypes.Type(
        value =
        OneOfTwo::class,
        name = "OneOfTwo"
    )
)
sealed class ParentOneOf() {
    abstract val discriminator: String
}
