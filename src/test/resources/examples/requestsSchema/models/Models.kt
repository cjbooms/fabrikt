package examples.requestsSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.Int
import kotlin.String

data class DummyRequest(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("age")
    @get:JsonProperty("age")
    val age: Int? = null
)

data class MultiContent0(
    @param:JsonProperty("a")
    @get:JsonProperty("a")
    val a: String? = null
)

data class MultiContent1(
    @param:JsonProperty("x")
    @get:JsonProperty("x")
    val x: Int? = null
)
