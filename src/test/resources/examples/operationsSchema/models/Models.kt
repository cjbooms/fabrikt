package examples.operationsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

enum class Paths1enumPostParameters0Schema(
    @JsonValue
    val value: String
) {
    X("X"),

    Y("Y");

    companion object {
        private val mapping: Map<String, Paths1enumPostParameters0Schema> =
            values().associateBy(Paths1enumPostParameters0Schema::value)

        fun fromValue(value: String): Paths1enumPostParameters0Schema? = mapping[value]
    }
}

enum class Paths1enumPostRequestBodyContentApplication1jsonSchema(
    @JsonValue
    val value: String
) {
    A("A"),

    B("B");

    companion object {
        private val mapping: Map<String, Paths1enumPostRequestBodyContentApplication1jsonSchema> =
            values().associateBy(Paths1enumPostRequestBodyContentApplication1jsonSchema::value)

        fun fromValue(value: String): Paths1enumPostRequestBodyContentApplication1jsonSchema? =
            mapping[value]
    }
}

data class Paths1objectPostParameters0Schema(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: Paths1objectPostParameters0SchemaProp2? = null
)

data class Paths1objectPostParameters0SchemaProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    val innerProp: Int? = null
)

data class Paths1objectPostRequestBodyContentApplication1jsonSchema(
    @param:JsonProperty("prop1")
    @get:JsonProperty("prop1")
    val prop1: Int? = null,
    @param:JsonProperty("prop2")
    @get:JsonProperty("prop2")
    @get:Valid
    val prop2: Paths1objectPostRequestBodyContentApplication1jsonSchemaProp2? = null
)

data class Paths1objectPostRequestBodyContentApplication1jsonSchemaInnerProp(
    @param:JsonProperty("inner_inner_prop")
    @get:JsonProperty("inner_inner_prop")
    val innerInnerProp: Int? = null
)

data class Paths1objectPostRequestBodyContentApplication1jsonSchemaProp2(
    @param:JsonProperty("inner_prop")
    @get:JsonProperty("inner_prop")
    @get:Valid
    val innerProp: Paths1objectPostRequestBodyContentApplication1jsonSchemaInnerProp? = null
)

data class Paths1simpleParameters0Schema(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)
