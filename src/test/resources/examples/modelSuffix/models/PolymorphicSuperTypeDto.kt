package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "generation",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = PolymorphicTypeOneDto::class, name = "PolymorphicTypeOne"))
public sealed class PolymorphicSuperTypeDto(
  public open val firstName: String,
  public open val lastName: String,
) {
  public abstract val generation: String
}
