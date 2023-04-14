package examples.wildCardTypes.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.String
import kotlin.collections.Map

data class WildCardTypes(
    @param:JsonProperty("user_name")
    @get:JsonProperty("user_name")
    @get:NotNull
    val userName: String,
    @param:JsonProperty("meta")
    @get:JsonProperty("meta")
    @get:NotNull
    val meta: Map<String, Any>,
)
