package examples.operationsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

enum class EnumPostParametersP(
    @JsonValue
    val value: String
) {
    X("X"),

    Y("Y");

    companion object {
        private val mapping: Map<String, EnumPostParametersP> =
            values().associateBy(EnumPostParametersP::value)

        fun fromValue(value: String): EnumPostParametersP? = mapping[value]
    }
}

enum class EnumPostRequestBody(
    @JsonValue
    val value: String
) {
    A("A"),

    B("B");

    companion object {
        private val mapping: Map<String, EnumPostRequestBody> =
            values().associateBy(EnumPostRequestBody::value)

        fun fromValue(value: String): EnumPostRequestBody? = mapping[value]
    }
}

data class ObjectPostParametersP(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostParametersProp2? = null
)

data class ObjectPostParametersProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

data class ObjectPostRequestBody(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostRequestBodyProp2? = null
)

data class ObjectPostRequestBodyInnerProp(
    @param:JsonProperty("inner_inner_prop")
    @get:JsonProperty("inner_inner_prop")
    val innerInnerProp: Int? = null
)

data class ObjectPostRequestBodyProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    @get:Valid
    val innerProp: ObjectPostRequestBodyInnerProp? = null
)

data class SimpleParametersPathP(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)
