package examples.nestedPolymorphicModels.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.Boolean
import kotlin.Int
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "secondLevelDiscriminator",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = ThirdLevelChild21::class, name =
    "thirdLevelChild1"),JsonSubTypes.Type(value = ThirdLevelChild22::class, name =
    "thirdLevelChild2"))
public sealed class SecondLevelChild2(
  @param:JsonProperty("rootField1")
  @get:JsonProperty("rootField1")
  @get:NotNull
  override val rootField1: String,
  @param:JsonProperty("rootField2")
  @get:JsonProperty("rootField2")
  override val rootField2: Boolean? = null,
  @param:JsonProperty("firstLevelField1")
  @get:JsonProperty("firstLevelField1")
  @get:NotNull
  override val firstLevelField1: String,
  @param:JsonProperty("firstLevelField2")
  @get:JsonProperty("firstLevelField2")
  override val firstLevelField2: Int? = null,
  public open val metadata: SecondLevelMetadata,
  @get:JsonProperty("firstLevelDiscriminator")
  @get:NotNull
  @param:JsonProperty("firstLevelDiscriminator")
  override val firstLevelDiscriminator: FirstLevelDiscriminator =
      FirstLevelDiscriminator.SECOND_LEVEL_CHILD2,
) : FirstLevelChild(rootField1, rootField2, firstLevelField1, firstLevelField2) {
  public abstract val secondLevelDiscriminator: SecondLevelDiscriminator
}

public data class ThirdLevelChild21(
  @param:JsonProperty("rootField1")
  @get:JsonProperty("rootField1")
  @get:NotNull
  override val rootField1: String,
  @param:JsonProperty("rootField2")
  @get:JsonProperty("rootField2")
  override val rootField2: Boolean? = null,
  @param:JsonProperty("firstLevelField1")
  @get:JsonProperty("firstLevelField1")
  @get:NotNull
  override val firstLevelField1: String,
  @param:JsonProperty("firstLevelField2")
  @get:JsonProperty("firstLevelField2")
  override val firstLevelField2: Int? = null,
  @param:JsonProperty("metadata")
  @get:JsonProperty("metadata")
  @get:NotNull
  @get:Valid
  override val metadata: SecondLevelMetadata,
  @param:JsonProperty("creationDate")
  @get:JsonProperty("creationDate")
  @get:NotNull
  public val creationDate: Int,
  @get:JsonProperty("secondLevelDiscriminator")
  @get:NotNull
  @param:JsonProperty("secondLevelDiscriminator")
  override val secondLevelDiscriminator: SecondLevelDiscriminator =
      SecondLevelDiscriminator.THIRD_LEVEL_CHILD1,
) : SecondLevelChild2(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata)

public data class ThirdLevelChild22(
  @param:JsonProperty("rootField1")
  @get:JsonProperty("rootField1")
  @get:NotNull
  override val rootField1: String,
  @param:JsonProperty("rootField2")
  @get:JsonProperty("rootField2")
  override val rootField2: Boolean? = null,
  @param:JsonProperty("firstLevelField1")
  @get:JsonProperty("firstLevelField1")
  @get:NotNull
  override val firstLevelField1: String,
  @param:JsonProperty("firstLevelField2")
  @get:JsonProperty("firstLevelField2")
  override val firstLevelField2: Int? = null,
  @param:JsonProperty("metadata")
  @get:JsonProperty("metadata")
  @get:NotNull
  @get:Valid
  override val metadata: SecondLevelMetadata,
  @param:JsonProperty("isDeleted")
  @get:JsonProperty("isDeleted")
  @get:NotNull
  public val isDeleted: Boolean,
  @get:JsonProperty("secondLevelDiscriminator")
  @get:NotNull
  @param:JsonProperty("secondLevelDiscriminator")
  override val secondLevelDiscriminator: SecondLevelDiscriminator =
      SecondLevelDiscriminator.THIRD_LEVEL_CHILD2,
) : SecondLevelChild2(rootField1, rootField2, firstLevelField1, firstLevelField2, metadata)
