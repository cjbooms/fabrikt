package examples.objectRef.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Int

data class ContainsObjectRef(
    @param:JsonProperty("weight_on_mars")
    @get:JsonProperty("weight_on_mars")
    @get:NotNull
    @get:Valid
    val weightOnMars: ObjectRef
)

data class ObjectRef(
    @param:JsonProperty("grams")
    @get:JsonProperty("grams")
    val grams: Int? = null
)
