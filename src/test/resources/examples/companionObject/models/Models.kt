package examples.companionObject.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.Long
import kotlin.String

data class Error(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    @get:NotNull
    val code: Int,
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    val message: String
) {
    companion object
}

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
) {
    companion object
}
