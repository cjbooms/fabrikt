package examples.polymorphicModels.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PolymorphicTypeOneAnotherRef(
  @get:NotNull
  override val firstName: String,
  @get:NotNull
  override val lastName: String,
  @SerialName("child_one_name")
  public val childOneName: String? = null,
) : PolymorphicSuperType(firstName, lastName)
