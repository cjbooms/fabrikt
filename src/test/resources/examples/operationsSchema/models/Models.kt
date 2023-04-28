package examples.operationsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

enum class EnumPostApplicationJsonRequestRequestBody(
    @JsonValue
    val value: String
) {
    A("A"),

    B("B");

    companion object {
        private val mapping: Map<String, EnumPostApplicationJsonRequestRequestBody> =
            values().associateBy(EnumPostApplicationJsonRequestRequestBody::value)

        fun fromValue(value: String): EnumPostApplicationJsonRequestRequestBody? = mapping[value]
    }
}

enum class EnumPostPParameterParameters(
    @JsonValue
    val value: String
) {
    X("X"),

    Y("Y");

    companion object {
        private val mapping: Map<String, EnumPostPParameterParameters> =
            values().associateBy(EnumPostPParameterParameters::value)

        fun fromValue(value: String): EnumPostPParameterParameters? = mapping[value]
    }
}

data class ObjectPostApplicationJsonRequest(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostApplicationJsonRequestProp2? = null
)

data class ObjectPostApplicationJsonRequestInnerProp(
    @param:JsonProperty("inner_inner_prop")
    @get:JsonProperty("inner_inner_prop")
    val innerInnerProp: Int? = null
)

data class ObjectPostApplicationJsonRequestProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    @get:Valid
    val innerProp: ObjectPostApplicationJsonRequestInnerProp? = null
)

data class ObjectPostPParameter(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ObjectPostPParameterProp2? = null
)

data class ObjectPostPParameterProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

data class SimplePathPParameter(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)
