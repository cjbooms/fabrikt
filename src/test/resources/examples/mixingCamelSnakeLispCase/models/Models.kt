package examples.mixingCamelSnakeLispCase.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class MixingCamelSnakeLispCase(
    @param:JsonProperty("camelCase")
    @get:JsonProperty("camelCase")
    public val camelCase: String? = null,
    @param:JsonProperty("snake_case")
    @get:JsonProperty("snake_case")
    public val snakeCase: String? = null,
    @param:JsonProperty("lisp-case")
    @get:JsonProperty("lisp-case")
    public val lispCase: String? = null,
    @param:JsonProperty("PascalCase")
    @get:JsonProperty("PascalCase")
    public val pascalCase: String? = null,
)
