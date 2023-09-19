package examples.mapExamples.models

import com.fasterxml.jackson.`annotation`.JsonAnyGetter
import com.fasterxml.jackson.`annotation`.JsonAnySetter
import com.fasterxml.jackson.`annotation`.JsonIgnore
import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap

public data class BasicObject(
    @param:JsonProperty("one")
    @get:JsonProperty("one")
    public val one: String? = null,
)

public data class ComplexObjectWithMapsOfMaps(
    @param:JsonProperty("list-others")
    @get:JsonProperty("list-others")
    @get:Valid
    public val listOthers: List<BasicObject>? = null,
    @param:JsonProperty("map-of-maps")
    @get:JsonProperty("map-of-maps")
    @get:Valid
    public val mapOfMaps: Map<String, Map<String, BasicObject>>? = null,
)

public data class ComplexObjectWithRefTypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, SomeRef> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, SomeRef> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: SomeRef) {
        properties[name] = value
    }
}

public data class ComplexObjectWithTypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, ComplexObjectWithTypedMapValue> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, ComplexObjectWithTypedMapValue> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: ComplexObjectWithTypedMapValue) {
        properties[name] = value
    }
}

public data class ComplexObjectWithTypedMapValue(
    @param:JsonProperty("other_text")
    @get:JsonProperty("other_text")
    public val otherText: String? = null,
    @param:JsonProperty("other_number")
    @get:JsonProperty("other_number")
    public val otherNumber: Int? = null,
)

public data class ComplexObjectWithUntypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class ExternalObjectFour(
    @param:JsonProperty("blah")
    @get:JsonProperty("blah")
    public val blah: String? = null,
)

public data class InlinedComplexObjectWithTypedMapValue(
    @param:JsonProperty("other_text")
    @get:JsonProperty("other_text")
    public val otherText: String? = null,
    @param:JsonProperty("other_number")
    @get:JsonProperty("other_number")
    public val otherNumber: Int? = null,
)

public data class InlinedTypedObjectMapValue(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
)

public data class MapHolder(
    @param:JsonProperty("wild_card")
    @get:JsonProperty("wild_card")
    public val wildCard: Map<String, Any>? = null,
    @param:JsonProperty("string_map")
    @get:JsonProperty("string_map")
    public val stringMap: Map<String, String>? = null,
    @param:JsonProperty("typed_object_map")
    @get:JsonProperty("typed_object_map")
    @get:Valid
    public val typedObjectMap: Map<String, TypedObjectMapValue>? = null,
    @param:JsonProperty("object_map")
    @get:JsonProperty("object_map")
    public val objectMap: Map<String, Map<String, Any>>? = null,
    @param:JsonProperty("inlined_string_map")
    @get:JsonProperty("inlined_string_map")
    public val inlinedStringMap: Map<String, String>? = null,
    @param:JsonProperty("inlined_object_map")
    @get:JsonProperty("inlined_object_map")
    public val inlinedObjectMap: Map<String, Map<String, Any>>? = null,
    @param:JsonProperty("inlined_unknown_map")
    @get:JsonProperty("inlined_unknown_map")
    public val inlinedUnknownMap: Map<String, Any>? = null,
    @param:JsonProperty("inlined_typed_object_map")
    @get:JsonProperty("inlined_typed_object_map")
    @get:Valid
    public val inlinedTypedObjectMap: Map<String, InlinedTypedObjectMapValue>? = null,
    @param:JsonProperty("complex_object_with_untyped_map")
    @get:JsonProperty("complex_object_with_untyped_map")
    @get:Valid
    public val complexObjectWithUntypedMap: ComplexObjectWithUntypedMap? = null,
    @param:JsonProperty("complex_object_with_typed_map")
    @get:JsonProperty("complex_object_with_typed_map")
    @get:Valid
    public val complexObjectWithTypedMap: ComplexObjectWithTypedMap? = null,
    @param:JsonProperty("inlined_complex_object_with_untyped_map")
    @get:JsonProperty("inlined_complex_object_with_untyped_map")
    @get:Valid
    public val inlinedComplexObjectWithUntypedMap: MapHolderInlinedComplexObjectWithUntypedMap? =
        null,
    @param:JsonProperty("inlined_complex_object_with_typed_map")
    @get:JsonProperty("inlined_complex_object_with_typed_map")
    @get:Valid
    public val inlinedComplexObjectWithTypedMap: MapHolderInlinedComplexObjectWithTypedMap? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, Map<String, ExternalObjectFour>> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, Map<String, ExternalObjectFour>> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Map<String, ExternalObjectFour>) {
        properties[name] = value
    }
}

public data class MapHolderInlinedComplexObjectWithTypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, InlinedComplexObjectWithTypedMapValue> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, InlinedComplexObjectWithTypedMapValue> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: InlinedComplexObjectWithTypedMapValue) {
        properties[name] = value
    }
}

public data class MapHolderInlinedComplexObjectWithUntypedMap(
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf(),
) {
    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class SomeRef(
    @param:JsonProperty("other_text")
    @get:JsonProperty("other_text")
    public val otherText: String? = null,
    @param:JsonProperty("other_number")
    @get:JsonProperty("other_number")
    public val otherNumber: Int? = null,
)

public data class TypedObjectMapValue(
    @param:JsonProperty("code")
    @get:JsonProperty("code")
    public val code: Int? = null,
    @param:JsonProperty("text")
    @get:JsonProperty("text")
    public val text: String? = null,
)
