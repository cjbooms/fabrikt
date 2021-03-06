package examples.anyOfOneOfAllOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
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

data class OneOfAndTopLevelProps(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    val firstProperty: String? = null,
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    val secondProperty: String? = null,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    val thirdProperty: String? = null,
    @param:JsonProperty("forth_property")
    @get:JsonProperty("forth_property")
    @get:NotNull
    val forthProperty: String,
    @param:JsonProperty("fifth_property")
    @get:JsonProperty("fifth_property")
    @get:NotNull
    val fifthProperty: OffsetDateTime
)

data class FirstOneB(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    val firstProperty: String? = null
)

data class SecondOneB(
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    val secondProperty: String? = null,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    val thirdProperty: String? = null
)
