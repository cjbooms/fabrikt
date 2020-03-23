package examples.anyOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

data class ContainsAnyOf(
    @param:JsonProperty("prop_one")
    @get:JsonProperty("prop_one")
    val propOne: String? = null,
    @param:JsonProperty("prop_two")
    @get:JsonProperty("prop_two")
    val propTwo: OffsetDateTime? = null,
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    val weightOnMars: Int? = null
)

data class SomeAnyOf(
    @param:JsonProperty("prop_one")
    @get:JsonProperty("prop_one")
    @get:NotNull
    val propOne: String,
    @param:JsonProperty("prop_two")
    @get:JsonProperty("prop_two")
    @get:NotNull
    val propTwo: OffsetDateTime
)
