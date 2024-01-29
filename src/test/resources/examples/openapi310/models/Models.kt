package examples.openapi310.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class NewNullableFormat(
    @param:JsonProperty("version")
    @get:JsonProperty("version")
    @get:NotNull
    public val version: String,
)
