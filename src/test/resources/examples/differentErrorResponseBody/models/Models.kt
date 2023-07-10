package examples.differentErrorResponseBody.models

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.String

data class ErrorResponse(
    @param:JsonProperty("errorMessage")
    @get:JsonProperty("errorMessage")
    val errorMessage: String? = null
)

data class SuccessResponse(
    @param:JsonProperty("successMessage")
    @get:JsonProperty("successMessage")
    val successMessage: String? = null
)
