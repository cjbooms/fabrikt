package examples.nullableListTypes.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

data class NullableListTypes(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("tags")
    @get:JsonProperty("tags")
    val tags: List<String>? = null
)
