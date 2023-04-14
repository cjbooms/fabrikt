package examples.mixingCamelSnakeLispCase.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.String

data class MixingCamelSnakeLispCase(
    @param:JsonProperty("camelCase")
    @get:JsonProperty("camelCase")
    val camelCase: String? = null,
    @param:JsonProperty("snake_case")
    @get:JsonProperty("snake_case")
    val snakeCase: String? = null,
    @param:JsonProperty("lisp-case")
    @get:JsonProperty("lisp-case")
    val lispCase: String? = null,
    @param:JsonProperty("PascalCase")
    @get:JsonProperty("PascalCase")
    val pascalCase: String? = null,
)
