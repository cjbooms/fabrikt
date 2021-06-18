package examples.externalReferences.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

data class ContainingExternalReference(
    @param:JsonProperty("some-external-reference")
    @get:JsonProperty("some-external-reference")
    @get:Valid
    val someExternalReference: ExternalObject? = null
)

enum class ExternalObjectThreeEnum(
    @JsonValue
    val value: String
) {
    ONE("one"),

    TWO("two"),

    THREE("three");
}

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

data class ExternalObjectTwo(
    @param:JsonProperty("list-others")
    @get:JsonProperty("list-others")
    @get:Valid
    val listOthers: List<ExternalObjectThree>? = null
)

data class ExternalObject(
    @param:JsonProperty("another")
    @get:JsonProperty("another")
    @get:Valid
    val another: ExternalObjectTwo? = null
)
