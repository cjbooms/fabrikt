package examples.nestedPolymorphicModels.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Int
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "rootDiscriminator",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = FirstLevelChild::class, name = "firstLevelChild"))
public sealed class RootType(
  public open val rootField1: String,
  public open val rootField2: Boolean? = null,
) {
  public abstract val rootDiscriminator: RootDiscriminator
}

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "firstLevelDiscriminator",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = SecondLevelChild1::class, name =
    "secondLevelChild1"),JsonSubTypes.Type(value = SecondLevelChild2::class, name =
    "secondLevelChild2"))
public sealed class FirstLevelChild(
  @param:JsonProperty("rootField1")
  @get:JsonProperty("rootField1")
  @get:NotNull
  override val rootField1: String,
  @param:JsonProperty("rootField2")
  @get:JsonProperty("rootField2")
  override val rootField2: Boolean? = null,
  public open val firstLevelField1: String,
  public open val firstLevelField2: Int? = null,
  @get:JsonProperty("rootDiscriminator")
  @get:NotNull
  @param:JsonProperty("rootDiscriminator")
  override val rootDiscriminator: RootDiscriminator = RootDiscriminator.FIRST_LEVEL_CHILD,
) : RootType(rootField1, rootField2) {
  public abstract val firstLevelDiscriminator: FirstLevelDiscriminator
}
