package examples.responsesSchema.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

data class ErrorResponseProblemJson(
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    val message: String,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    @get:NotNull
    val code: Int
)

data class SuccessResponseJson(
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    val message: String
)
