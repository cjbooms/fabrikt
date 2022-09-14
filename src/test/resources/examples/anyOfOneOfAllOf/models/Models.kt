package examples.anyOfOneOfAllOf.models

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.collections.Map
import kotlin.collections.MutableMap

data class ComplexParent(
    @param:JsonProperty("oneOf")
    @get:JsonProperty("oneOf")
    val oneOf: Any? = null,
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null,
    @param:JsonProperty("required_string")
    @get:JsonProperty("required_string")
    @get:NotNull
    val requiredString: String,
    @param:JsonProperty("required_int")
    @get:JsonProperty("required_int")
    @get:NotNull
    val requiredInt: Int,
    @param:JsonProperty("top_level_prop")
    @get:JsonProperty("top_level_prop")
    @get:NotNull
    val topLevelProp: BigDecimal
)

data class ComplexSecondOneA(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    @get:NotNull
    val moreNestedPropOne: String
)

data class ContainsNestedAnyOf(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null
)

data class FirstAnyA(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null
)

data class FirstOneA(
    @param:JsonProperty("first_nested_one_of_prop")
    @get:JsonProperty("first_nested_one_of_prop")
    val firstNestedOneOfProp: String? = null
)

data class FirstOneB(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    val firstProperty: String? = null
)

data class MoreNesting(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    @get:NotNull
    val moreNestedPropOne: String
)

data class OneOfAdditionalProps(
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Any> = properties

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class SecondAnyA(
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null
)

data class SecondOneB(
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    val secondProperty: String? = null,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    val thirdProperty: String? = null,
    @param:JsonProperty("forth_property")
    @get:JsonProperty("forth_property")
    val forthProperty: Any? = null
)

data class SimpleOneOfs(
    @param:JsonProperty("oneof_property")
    @get:JsonProperty("oneof_property")
    val oneofProperty: Any? = null,
    @param:JsonProperty("primitive_oneof_property")
    @get:JsonProperty("primitive_oneof_property")
    val primitiveOneofProperty: Any? = null
)

data class SimpleTypeWithRequiredProps(
    @param:JsonProperty("required_string")
    @get:JsonProperty("required_string")
    @get:NotNull
    val requiredString: String,
    @param:JsonProperty("required_int")
    @get:JsonProperty("required_int")
    @get:NotNull
    val requiredInt: Int
)
