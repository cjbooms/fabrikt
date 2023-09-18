package examples.instantDateTime.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.Instant
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.List

public data class FirstModel(
    @param:JsonProperty("date")
    @get:JsonProperty("date")
    public val date: Instant? = null,
    @param:JsonProperty("extra_first_attr")
    @get:JsonProperty("extra_first_attr")
    public val extraFirstAttr: List<Instant>? = null,
)

public data class QueryResult(
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<FirstModel>,
)
