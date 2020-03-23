package examples.simpleRef.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Int

data class ContainsSimpleRef(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    val weightOnMars: Int
)
