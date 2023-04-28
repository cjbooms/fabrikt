package examples.operationsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

enum class Parameters(
    @JsonValue
    val value: String
) {
    X("X"),

    Y("Y");

    companion object {
        private val mapping: Map<String, Parameters> = values().associateBy(Parameters::value)

        fun fromValue(value: String): Parameters? = mapping[value]
    }
}

data class ParametersProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

enum class RequestBody(
    @JsonValue
    val value: String
) {
    A("A"),

    B("B");

    companion object {
        private val mapping: Map<String, RequestBody> = values().associateBy(RequestBody::value)

        fun fromValue(value: String): RequestBody? = mapping[value]
    }
}

data class RequestBodyProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

data class TODOParamObjectpost(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: ParametersProp2? = null
)

data class TODOParamSimple(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)

data class TODORequestObjectpost(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: RequestBodyProp2? = null
)
