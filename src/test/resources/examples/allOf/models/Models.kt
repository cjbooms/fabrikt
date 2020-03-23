package examples.allOf.models

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

data class ContainsAllOf(
    @param:JsonProperty("prop_one")
    @get:JsonProperty("prop_one")
    @get:NotNull
    val propOne: String,
    @param:JsonProperty("prop_two")
    @get:JsonProperty("prop_two")
    @get:NotNull
    val propTwo: OffsetDateTime,
    @param:JsonProperty("primitive_allOf")
    @get:JsonProperty("primitive_allOf")
    @get:DecimalMin(
        value = "1",
        inclusive = true
    )
    @get:DecimalMax(
        value = "5",
        inclusive = true
    )
    val primitiveAllOf: Int? = null,
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    val weightOnMars: Int
)

data class SomeAllOf(
    @param:JsonProperty("prop_one")
    @get:JsonProperty("prop_one")
    @get:NotNull
    val propOne: String,
    @param:JsonProperty("prop_two")
    @get:JsonProperty("prop_two")
    @get:NotNull
    val propTwo: OffsetDateTime,
    @param:JsonProperty("primitive_allOf")
    @get:JsonProperty("primitive_allOf")
    @get:DecimalMin(
        value = "1",
        inclusive = true
    )
    @get:DecimalMax(
        value = "5",
        inclusive = true
    )
    val primitiveAllOf: Int? = null
)
