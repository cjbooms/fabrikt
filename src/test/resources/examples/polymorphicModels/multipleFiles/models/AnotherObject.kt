package examples.polymorphicModels.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int

public data class AnotherObject(
  @param:JsonProperty("some_integer_propery")
  @get:JsonProperty("some_integer_propery")
  public val someIntegerPropery: Int? = null,
)
