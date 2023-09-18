package examples.validationAnnotations.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class ValidationAnnotations(
    @param:JsonProperty("user_name")
    @get:JsonProperty("user_name")
    @get:NotNull
    @get:Pattern(regexp = "[a-zA-Z]")
    public val userName: String,
    @param:JsonProperty("age")
    @get:JsonProperty("age")
    @get:NotNull
    @get:DecimalMin(
        value = "0",
        inclusive = false,
    )
    @get:DecimalMax(
        value = "100",
        inclusive = true,
    )
    public val age: Int,
    @param:JsonProperty("bio")
    @get:JsonProperty("bio")
    @get:NotNull
    @get:Size(
        min = 20,
        max = 200,
    )
    public val bio: String,
    @param:JsonProperty("friends")
    @get:JsonProperty("friends")
    @get:NotNull
    @get:Size(
        min = 0,
        max = 10,
    )
    public val friends: List<String>,
)
