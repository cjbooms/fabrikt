package examples.complexAnyOneAllCombo.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import kotlin.String

data class ComplexParent(
    @param:JsonProperty("first_nested_one_of_prop")
    @get:JsonProperty("first_nested_one_of_prop")
    val firstNestedOneOfProp: String? = null,
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    val moreNestedPropOne: String? = null,
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null,
    @param:JsonProperty("top_level_prop")
    @get:JsonProperty("top_level_prop")
    val topLevelProp: BigDecimal? = null
)

data class ContainsNestedOneOf(
    @param:JsonProperty("first_nested_one_of_prop")
    @get:JsonProperty("first_nested_one_of_prop")
    val firstNestedOneOfProp: String? = null,
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    val moreNestedPropOne: String? = null
)

data class ContainsNestedAnyOf(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null,
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null
)

data class FirstOneA(
    @param:JsonProperty("first_nested_one_of_prop")
    @get:JsonProperty("first_nested_one_of_prop")
    val firstNestedOneOfProp: String? = null
)

data class ComplexSecondOneA(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    val moreNestedPropOne: String? = null
)

data class MoreNesting(
    @param:JsonProperty("more_nested_prop_one")
    @get:JsonProperty("more_nested_prop_one")
    val moreNestedPropOne: String? = null
)

data class FirstAnyA(
    @param:JsonProperty("first_nested_any_of_prop")
    @get:JsonProperty("first_nested_any_of_prop")
    val firstNestedAnyOfProp: String? = null
)

data class SecondAnyA(
    @param:JsonProperty("second_nested_any_of_prop")
    @get:JsonProperty("second_nested_any_of_prop")
    val secondNestedAnyOfProp: String? = null
)
