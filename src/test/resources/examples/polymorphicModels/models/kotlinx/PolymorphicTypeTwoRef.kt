package examples.polymorphicModels.models

import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PolymorphicTypeTwoRef(
  @get:NotNull
  override val firstName: String,
  @get:NotNull
  override val lastName: String,
  @SerialName("some_integer_propery")
  public val someIntegerPropery: Int? = null,
  @SerialName("child_two_age")
  public val childTwoAge: Int? = null,
) : PolymorphicSuperType(firstName, lastName)
