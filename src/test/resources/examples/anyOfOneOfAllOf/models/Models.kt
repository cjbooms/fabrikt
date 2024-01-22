package examples.anyOfOneOfAllOf.models

import com.fasterxml.jackson.`annotation`.JsonAnyGetter
import com.fasterxml.jackson.`annotation`.JsonAnySetter
import com.fasterxml.jackson.`annotation`.JsonIgnore
import com.fasterxml.jackson.`annotation`.JsonProperty
import java.math.BigDecimal
import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlin.collections.MutableMap

public data class ComplexParent(
    @param:JsonProperty("oneOf")
    @get:JsonProperty("oneOf")
    public val oneOf: Any? = null,
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    public val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    public val secondNestedAnyOfProp: String? = null,
    @param:JsonProperty("required_string")
    @get:JsonProperty("required_string")
    @get:NotNull
    public val requiredString: String,
    @param:JsonProperty("required_int")
    @get:JsonProperty("required_int")
    @get:NotNull
    public val requiredInt: Int,
    @param:JsonProperty("top_level_prop")
    @get:JsonProperty("top_level_prop")
    @get:NotNull
    public val topLevelProp: BigDecimal,
)

public data class ComplexSecondOneA(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    @get:NotNull
    public val moreNestedPropOne: String,
)

public data class ContainsNestedAnyOf(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    public val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    public val secondNestedAnyOfProp: String? = null,
)

public data class FirstAnyA(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    public val firstNestedAnyOfProp: String? = null,
)

public data class FirstOneA(
    @param:JsonProperty("first_nested_one_of_prop")
    @get:JsonProperty("first_nested_one_of_prop")
    public val firstNestedOneOfProp: String? = null,
)

public data class FirstOneB(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    public val firstProperty: String? = null,
)

public data class MoreNesting(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    @get:NotNull
    public val moreNestedPropOne: String,
)

public data class OneOfAdditionalProps(
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    public val secondNestedAnyOfProp: String? = null,
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class SecondAnyA(
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    public val secondNestedAnyOfProp: String? = null,
)

public data class SecondOneB(
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    public val secondProperty: String? = null,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    public val thirdProperty: String? = null,
    @param:JsonProperty("forth_property")
    @get:JsonProperty("forth_property")
    public val forthProperty: Any? = null,
)

public data class SimpleOneOfs(
    @param:JsonProperty("oneof_property")
    @get:JsonProperty("oneof_property")
    public val oneofProperty: Any? = null,
    @param:JsonProperty("primitive_oneof_property")
    @get:JsonProperty("primitive_oneof_property")
    public val primitiveOneofProperty: Any? = null,
)

public data class SimpleTypeWithRequiredProps(
    @param:JsonProperty("required_string")
    @get:JsonProperty("required_string")
    @get:NotNull
    public val requiredString: String,
    @param:JsonProperty("required_int")
    @get:JsonProperty("required_int")
    @get:NotNull
    public val requiredInt: Int,
)
