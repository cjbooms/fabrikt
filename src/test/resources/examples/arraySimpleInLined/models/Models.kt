package examples.arraySimpleInLined.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.collections.List

data class ArraySimpleInLined(
    @param:JsonProperty("quantities")
    @get:JsonProperty("quantities")
    @get:NotNull
    val quantities: List<Long>
)
