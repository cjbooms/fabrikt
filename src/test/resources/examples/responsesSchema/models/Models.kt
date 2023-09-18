package examples.responsesSchema.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

public data class ErrorResponse(
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    public val message: String,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    @get:NotNull
    public val code: Int,
)

public data class SucccessResponse(
    @param:JsonProperty("message")
    @get:JsonProperty("message")
    @get:NotNull
    public val message: String,
)
