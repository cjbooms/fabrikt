package examples.instantDateTime.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.List

data class FirstModel(
    @param:JsonProperty("date")
    @get:JsonProperty("date")
    val date: Instant? = null,
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    val extraFirstAttr: List<Instant>? = null
)

data class QueryResult(
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<FirstModel>
)
