package examples.oneOfPolymorphicModels.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.constraints.NotNull
import kotlin.Int
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "shared",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = PolymorphicTypeTwoA::class, name =
    "PolymorphicTypeTwoA"),JsonSubTypes.Type(value = PolymorphicTypeTwoB::class, name =
    "PolymorphicTypeTwoB"))
public sealed class PolymorphicSuperTypeTwo() {
  public abstract val shared: String
}

public data class PolymorphicTypeTwoA(
  @param:JsonProperty("whateverC")
  @get:JsonProperty("whateverC")
  public val whateverC: String? = null,
  @get:JsonProperty("shared")
  @get:NotNull
  @param:JsonProperty("shared")
  override val shared: String = "PolymorphicTypeTwoA",
) : PolymorphicSuperTypeTwo()

public data class PolymorphicTypeTwoB(
  @param:JsonProperty("whateverD")
  @get:JsonProperty("whateverD")
  public val whateverD: Int? = null,
  @get:JsonProperty("shared")
  @get:NotNull
  @param:JsonProperty("shared")
  override val shared: String = "PolymorphicTypeTwoB",
) : PolymorphicSuperTypeTwo()
