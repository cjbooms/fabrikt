package examples.sharingObjectDefinitions.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int

data class FirstContainingShared(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:Valid
    val weightOnMars: SharedDefinition? = null
)

data class SharedDefinition(
    @param:JsonProperty("grams")
    @get:JsonProperty("grams")
    val grams: Int? = null
)

data class SecondContainingShared(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    @get:Valid
    val weightOnMars: SharedDefinition
)
