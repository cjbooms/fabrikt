package examples.pathLevelParameters.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class JsonEncodedHeader(
    @param:JsonProperty("headerProp1")
    @get:JsonProperty("headerProp1")
    public val headerProp1: String? = null,
    @param:JsonProperty("headerProp2")
    @get:JsonProperty("headerProp2")
    public val headerProp2: String? = null,
)
