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
  property = "type",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = ChildTypeA::class, name =
    "CHILD_TYPE_A"),JsonSubTypes.Type(value = ChildTypeB::class, name = "CHILD_TYPE_B"))
public sealed class ParentSpec() {
  public abstract val type: ParentType
}

public data class ChildTypeA(
  @param:JsonProperty("some_string")
  @get:JsonProperty("some_string")
  @get:NotNull
  public val someString: String,
  @get:JsonProperty("type")
  @get:NotNull
  @param:JsonProperty("type")
  override val type: ParentType = ParentType.CHILD_TYPE_A,
) : ParentSpec()

public data class ChildTypeB(
  @param:JsonProperty("some_int")
  @get:JsonProperty("some_int")
  @get:NotNull
  public val someInt: Int,
  @get:JsonProperty("type")
  @get:NotNull
  @param:JsonProperty("type")
  override val type: ParentType = ParentType.CHILD_TYPE_B,
) : ParentSpec()
