package examples.putApi.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

data class Contributor(
    @param:JsonProperty("username")
    @get:JsonProperty("username")
    @get:NotNull
    val username: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    val name: String? = null
)
