package examples.enumPolymorphicDiscriminator.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "some_enum",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = DiscriminatedChild1::class, name =
    "obj_one_only"),JsonSubTypes.Type(value = DiscriminatedChild2::class, name =
    "obj_two_first"),JsonSubTypes.Type(value = DiscriminatedChild2::class, name =
    "obj_two_second"),JsonSubTypes.Type(value = DiscriminatedChild3::class, name = "obj_three"))
public sealed class ChildDefinition(
  public open val inlineObj: ChildDefinitionInlineObj? = null,
  public open val inlineArray: List<ChildDefinitionInlineArray>? = null,
  public open val inlineEnum: ChildDefinitionInlineEnum? = null,
) {
  public abstract val someEnum: ChildDiscriminator
}

public data class DiscriminatedChild1(
  @param:JsonProperty("inline_obj")
  @get:JsonProperty("inline_obj")
  @get:Valid
  override val inlineObj: ChildDefinitionInlineObj? = null,
  @param:JsonProperty("inline_array")
  @get:JsonProperty("inline_array")
  @get:Valid
  override val inlineArray: List<ChildDefinitionInlineArray>? = null,
  @param:JsonProperty("inline_enum")
  @get:JsonProperty("inline_enum")
  override val inlineEnum: ChildDefinitionInlineEnum? = null,
  @param:JsonProperty("some_prop")
  @get:JsonProperty("some_prop")
  public val someProp: String? = null,
  @get:JsonProperty("some_enum")
  @get:NotNull
  @param:JsonProperty("some_enum")
  override val someEnum: ChildDiscriminator = ChildDiscriminator.OBJ_ONE_ONLY,
) : ChildDefinition(inlineObj, inlineArray, inlineEnum)

public data class DiscriminatedChild2(
  @get:JsonProperty("some_enum")
  @get:NotNull
  override val someEnum: ChildDiscriminator,
  @param:JsonProperty("inline_obj")
  @get:JsonProperty("inline_obj")
  @get:Valid
  override val inlineObj: ChildDefinitionInlineObj? = null,
  @param:JsonProperty("inline_array")
  @get:JsonProperty("inline_array")
  @get:Valid
  override val inlineArray: List<ChildDefinitionInlineArray>? = null,
  @param:JsonProperty("inline_enum")
  @get:JsonProperty("inline_enum")
  override val inlineEnum: ChildDefinitionInlineEnum? = null,
  @param:JsonProperty("some_prop")
  @get:JsonProperty("some_prop")
  public val someProp: String? = null,
) : ChildDefinition(inlineObj, inlineArray, inlineEnum)

public data class DiscriminatedChild3(
  @param:JsonProperty("inline_obj")
  @get:JsonProperty("inline_obj")
  @get:Valid
  override val inlineObj: ChildDefinitionInlineObj? = null,
  @param:JsonProperty("inline_array")
  @get:JsonProperty("inline_array")
  @get:Valid
  override val inlineArray: List<ChildDefinitionInlineArray>? = null,
  @param:JsonProperty("inline_enum")
  @get:JsonProperty("inline_enum")
  override val inlineEnum: ChildDefinitionInlineEnum? = null,
  @get:JsonProperty("some_enum")
  @get:NotNull
  @param:JsonProperty("some_enum")
  override val someEnum: ChildDiscriminator = ChildDiscriminator.OBJ_THREE,
) : ChildDefinition(inlineObj, inlineArray, inlineEnum)
