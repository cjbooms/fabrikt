package examples.arrays.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List

public data class ArrayComplexInLined(
    @param:JsonProperty("quantities")
    @get:JsonProperty("quantities")
    @get:NotNull
    @get:Valid
    public val quantities: List<ArrayComplexInLinedQuantities>,
)

public data class ArrayComplexInLinedQuantities(
    @param:JsonProperty("prop_one")
    @get:JsonProperty("prop_one")
    public val propOne: String? = null,
)

public data class ArrayRef(
    @param:JsonProperty("grams")
    @get:JsonProperty("grams")
    public val grams: Int? = null,
)

public data class ArraySimpleInLined(
    @param:JsonProperty("quantities")
    @get:JsonProperty("quantities")
    @get:NotNull
    public val quantities: List<Long>,
)

public data class ContainsArrayOfArrays(
    @param:JsonProperty("array_of_arrays")
    @get:JsonProperty("array_of_arrays")
    @get:Valid
    public val arrayOfArrays: List<List<Something>>? = null,
)

public data class ContainsArrayRef(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    @get:Valid
    public val weightOnMars: List<ArrayRef>,
)

public data class Something(
    @param:JsonProperty("some_value")
    @get:JsonProperty("some_value")
    public val someValue: Int? = null,
)
