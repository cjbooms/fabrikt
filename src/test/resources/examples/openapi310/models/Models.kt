package examples.openapi310.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class NewNullableFormat(
    @param:JsonProperty("version")
    @get:JsonProperty("version")
    public val version: String?,
)
