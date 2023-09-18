package examples.parameterNameClash.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class SomeObject(
    @param:JsonProperty("test")
    @get:JsonProperty("test")
    public val test: String? = null,
)
