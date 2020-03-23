package examples.arrayOfArrays.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import kotlin.Int
import kotlin.collections.List

data class ContainsArrayOfArrays(
    @param:JsonProperty("array_of_arrays")
    @get:JsonProperty("array_of_arrays")
    @get:Valid
    val arrayOfArrays: List<List<Something>>? = null
)

data class Something(
    @param:JsonProperty("some_value")
    @get:JsonProperty("some_value")
    val someValue: Int? = null
)
