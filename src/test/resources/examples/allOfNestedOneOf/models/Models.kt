package examples.allOfNestedOneOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import kotlin.String

data class ContainsAllOfWithNestedOneOf(
    @param:JsonProperty("nested_one_of_prop")
    @get:JsonProperty("nested_one_of_prop")
    val nestedOneOfProp: String? = null,
    @param:JsonProperty("top_level_prop")
    @get:JsonProperty("top_level_prop")
    val topLevelProp: BigDecimal? = null
)

data class ContainsNestedOneOfA(
    @param:JsonProperty("nested_one_of_prop")
    @get:JsonProperty("nested_one_of_prop")
    val nestedOneOfProp: String? = null
)

data class TheOne(
    @param:JsonProperty("nested_one_of_prop")
    @get:JsonProperty("nested_one_of_prop")
    val nestedOneOfProp: String? = null
)
