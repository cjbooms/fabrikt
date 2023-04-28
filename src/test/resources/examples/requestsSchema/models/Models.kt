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
