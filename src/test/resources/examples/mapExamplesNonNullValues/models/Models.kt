package examples.mapExamplesNonNullValues.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map

public data class Container(
    @param:JsonProperty("string_map")
    @get:JsonProperty("string_map")
    @get:NotNull
    public val stringMap: Map<String, String>,
    @param:JsonProperty("typed_object_map")
    @get:JsonProperty("typed_object_map")
    @get:NotNull
    @get:Valid
    public val typedObjectMap: Map<String, TypedObjectMapValue>,
    @param:JsonProperty("object_map")
    @get:JsonProperty("object_map")
    @get:NotNull
    public val objectMap: Map<String, Map<String, Any>>,
)

public data class TypedObjectMapValue(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
)
