package examples.oneOfPolymorphicModels.models

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
@JsonSubTypes(JsonSubTypes.Type(value = PolymorphicTypeOneA::class, name =
    "PolymorphicTypeOneA"),JsonSubTypes.Type(value = PolymorphicTypeOneB::class, name =
    "PolymorphicTypeOneB"))
public sealed class PolymorphicSuperTypeOne() {
  public abstract val shared: String
}

public data class PolymorphicTypeOneA(
  @param:JsonProperty("whateverA")
  @get:JsonProperty("whateverA")
  public val whateverA: String? = null,
  @get:JsonProperty("shared")
  @get:NotNull
  @param:JsonProperty("shared")
  override val shared: String = "PolymorphicTypeOneA",
) : PolymorphicSuperTypeOne()

public data class PolymorphicTypeOneB(
  @param:JsonProperty("whateverB")
  @get:JsonProperty("whateverB")
  public val whateverB: Int? = null,
  @get:JsonProperty("shared")
  @get:NotNull
  @param:JsonProperty("shared")
  override val shared: String = "PolymorphicTypeOneB",
) : PolymorphicSuperTypeOne()
