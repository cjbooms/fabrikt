package examples.operationsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

enum class EnumPostApplicationJsonRequestBody(
    @JsonValue
    val value: String
) {
    A("A"),

    B("B");

    companion object {
        private val mapping: Map<String, EnumPostApplicationJsonRequestBody> =
            values().associateBy(EnumPostApplicationJsonRequestBody::value)

        fun fromValue(value: String): EnumPostApplicationJsonRequestBody? = mapping[value]
    }
}

enum class EnumPostPParameters(
    @JsonValue
    val value: String
) {
    X("X"),

    Y("Y");

    companion object {
        private val mapping: Map<String, EnumPostPParameters> =
            values().associateBy(EnumPostPParameters::value)

        fun fromValue(value: String): EnumPostPParameters? = mapping[value]
    }
}

data class ObjectPostApplicationJsonRequestBody(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostApplicationJsonRequestBodyProp2? = null
)

data class ObjectPostApplicationJsonRequestBodyInnerProp(
    @param:JsonProperty("inner_inner_prop")
    @get:JsonProperty("inner_inner_prop")
    val innerInnerProp: Int? = null
)

data class ObjectPostApplicationJsonRequestBodyProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    @get:Valid
    val innerProp: ObjectPostApplicationJsonRequestBodyInnerProp? = null
)

data class ObjectPostPParameters(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostPParametersProp2? = null
)

data class ObjectPostPParametersProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

data class SimplePathPParameters(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)
