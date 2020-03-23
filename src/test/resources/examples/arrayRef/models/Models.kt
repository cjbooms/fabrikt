package examples.arrayRef.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.collections.List

data class ContainsArrayRef(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    @get:Valid
    val weightOnMars: List<ArrayRef>
)

data class ArrayRef(
    @param:JsonProperty("grams")
    @get:JsonProperty("grams")
    val grams: Int? = null
)
