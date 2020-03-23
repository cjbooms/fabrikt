package examples.snakeToCamel.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

data class SnakeToCamel(
    @param:JsonProperty("user_name")
    @get:JsonProperty("user_name")
    @get:NotNull
    val userName: String
)
