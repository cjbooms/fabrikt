package examples.optionalVsRequired.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

data class OptionalVsRequired(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("gender")
    @get:JsonProperty("gender")
    val gender: String? = null
)
