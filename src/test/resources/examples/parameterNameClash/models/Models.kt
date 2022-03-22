package examples.parameterNameClash.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.String

data class SomeObject(
    @param:JsonProperty("test")
    @get:JsonProperty("test")
    val test: String? = null
)
