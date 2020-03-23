package examples.deepNestedReferences.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.collections.List

data class ContainsDeepReferences(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:Valid
    val weightOnMars: List<FirstLevelArrayRef>? = null
)

data class FirstLevelArrayRef(
    @param:JsonProperty("gravity")
    @get:JsonProperty("gravity")
    @get:Valid
    val gravity: SecondLevelObjectRef? = null,
    @param:JsonProperty("weights")
    @get:JsonProperty("weights")
    @get:Valid
    val weights: List<SecondLevelArrayRef>? = null
)

data class ThirdLevelObject(
    @param:JsonProperty("third")
    @get:JsonProperty("third")
    val third: String? = null
)

data class SecondLevelObjectRef(
    @param:JsonProperty("gravity")
    @get:JsonProperty("gravity")
    val gravity: Int? = null,
    @param:JsonProperty("third_level")
    @get:JsonProperty("third_level")
    @get:Valid
    val thirdLevel: ThirdLevelObject? = null
)

data class SecondLevelArrayRef(
    @param:JsonProperty("grams")
    @get:JsonProperty("grams")
    val grams: Int? = null
)
