package examples.mapExamples.models

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlin.collections.MutableMap

data class MapHolder(
    @param:JsonProperty("wild_card")
    @get:JsonProperty("wild_card")
    val wildCard: Map<String, Any>? = null,
    @param:JsonProperty("string_map")
    @get:JsonProperty("string_map")
    val stringMap: Map<String, String>? = null,
    @param:JsonProperty("typed_object_map")
    @get:JsonProperty("typed_object_map")
    @get:Valid
    val typedObjectMap: Map<String, TypedObjectMapValue>? = null,
    @param:JsonProperty("object_map")
    @get:JsonProperty("object_map")
    val objectMap: Map<String, Map<String, Any>>? = null,
    @param:JsonProperty("inlined_string_map")
    @get:JsonProperty("inlined_string_map")
    val inlinedStringMap: Map<String, String>? = null,
    @param:JsonProperty("inlined_object_map")
    @get:JsonProperty("inlined_object_map")
    val inlinedObjectMap: Map<String, Map<String, Any>>? = null,
    @param:JsonProperty("inlined_unknown_map")
    @get:JsonProperty("inlined_unknown_map")
    val inlinedUnknownMap: Map<String, Any>? = null,
    @param:JsonProperty("inlined_typed_object_map")
    @get:JsonProperty("inlined_typed_object_map")
    @get:Valid
    val inlinedTypedObjectMap: Map<String, InlinedTypedObjectMapValue>? = null,
    @param:JsonProperty("complex_object_with_untyped_map")
    @get:JsonProperty("complex_object_with_untyped_map")
    @get:Valid
    val complexObjectWithUntypedMap: ComplexObjectWithUntypedMap? = null,
    @param:JsonProperty("complex_object_with_typed_map")
    @get:JsonProperty("complex_object_with_typed_map")
    @get:Valid
    val complexObjectWithTypedMap: ComplexObjectWithTypedMap? = null,
    @param:JsonProperty("inlined_complex_object_with_untyped_map")
    @get:JsonProperty("inlined_complex_object_with_untyped_map")
    @get:Valid
    val inlinedComplexObjectWithUntypedMap: InlinedComplexObjectWithUntypedMap? = null,
    @param:JsonProperty("inlined_complex_object_with_typed_map")
    @get:JsonProperty("inlined_complex_object_with_typed_map")
    @get:Valid
    val inlinedComplexObjectWithTypedMap: InlinedComplexObjectWithTypedMap? = null
)

data class TypedObjectMapValue(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null,
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null
)

data class InlinedTypedObjectMapValue(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null,
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null
)

data class ComplexObjectWithUntypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null
) {
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class ComplexObjectWithTypedMapValue(
    @param:JsonProperty("other_text")
    @get:JsonProperty("other_text")
    val otherText: String? = null,
    @param:JsonProperty("other_number")
    @get:JsonProperty("other_number")
    val otherNumber: Int? = null
)

data class ComplexObjectWithTypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null
) {
    val properties: MutableMap<String, ComplexObjectWithTypedMapValue> = mutableMapOf()

    @JsonAnySetter
    fun set(name: String, value: ComplexObjectWithTypedMapValue) {
        properties[name] = value
    }
}

data class InlinedComplexObjectWithUntypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null
) {
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class InlinedComplexObjectWithTypedMapValue(
    @param:JsonProperty("other_text")
    @get:JsonProperty("other_text")
    val otherText: String? = null,
    @param:JsonProperty("other_number")
    @get:JsonProperty("other_number")
    val otherNumber: Int? = null
)

data class InlinedComplexObjectWithTypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    val code: Int? = null
) {
    val properties: MutableMap<String, InlinedComplexObjectWithTypedMapValue> = mutableMapOf()

    @JsonAnySetter
    fun set(name: String, value: InlinedComplexObjectWithTypedMapValue) {
        properties[name] = value
    }
}
