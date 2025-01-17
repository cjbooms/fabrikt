package examples.polymorphicModels.models

import kotlin.Int
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AnotherObject(
  @SerialName("some_integer_propery")
  public val someIntegerPropery: Int? = null,
)
