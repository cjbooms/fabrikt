package examples.oneOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

data class OneOf(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    val firstProperty: String? = null,
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    val secondProperty: String? = null,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    val thirdProperty: String? = null
)

data class FirstOneC(
    @param:JsonProperty("first_property")
    @get:JsonProperty("first_property")
    @get:NotNull
    val firstProperty: String
)

data class SecondOneC(
    @param:JsonProperty("second_property")
    @get:JsonProperty("second_property")
    @get:NotNull
    val secondProperty: String,
    @param:JsonProperty("third_property")
    @get:JsonProperty("third_property")
    val thirdProperty: String? = null
)
