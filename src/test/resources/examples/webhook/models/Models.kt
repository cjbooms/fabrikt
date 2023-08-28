package examples.webhook.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.String

data class Pet(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    @get:NotNull
    val id: Long,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("tag")
    @get:JsonProperty("tag")
    val tag: String? = null
)
