package examples.polymorphicModels.models

import kotlin.String
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@JsonClassDiscriminator("generation")
@ExperimentalSerializationApi
@Serializable
public sealed class PolymorphicSuperType(
  @SerialName("first_name")
  public open val firstName: String,
  @SerialName("last_name")
  public open val lastName: String,
)
