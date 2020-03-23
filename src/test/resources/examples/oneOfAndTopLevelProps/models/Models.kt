package examples.oneOfAndTopLevelProps.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.String

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
