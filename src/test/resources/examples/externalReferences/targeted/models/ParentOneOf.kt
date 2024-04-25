package examples.externalReferences.targeted.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import javax.validation.constraints.NotNull
import kotlin.String

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.EXISTING_PROPERTY,
  property = "discriminator",
  visible = true,
)
@JsonSubTypes(JsonSubTypes.Type(value = OneOfOne::class, name = "OneOfOne"),JsonSubTypes.Type(value
    = OneOfTwo::class, name = "OneOfTwo"))
public sealed class ParentOneOf() {
  public abstract val discriminator: String
}

public data class OneOfOne(
  @param:JsonProperty("oneOfOne")
  @get:JsonProperty("oneOfOne")
  public val oneOfOne: String? = null,
  @get:JsonProperty("discriminator")
  @get:NotNull
  @param:JsonProperty("discriminator")
  override val discriminator: String = "OneOfOne",
) : ParentOneOf()

public data class OneOfTwo(
  @param:JsonProperty("oneOfTwo")
  @get:JsonProperty("oneOfTwo")
  public val oneOfTwo: String? = null,
  @get:JsonProperty("discriminator")
  @get:NotNull
  @param:JsonProperty("discriminator")
  override val discriminator: String = "OneOfTwo",
) : ParentOneOf()
