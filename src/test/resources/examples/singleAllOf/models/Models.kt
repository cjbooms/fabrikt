package examples.singleAllOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.String

data class Base(
    @param:JsonProperty("foo")
    @get:JsonProperty("foo")
    val foo: String? = null
)

data class Result(
    @param:JsonProperty("foo")
    @get:JsonProperty("foo")
    val foo: String? = null
)
