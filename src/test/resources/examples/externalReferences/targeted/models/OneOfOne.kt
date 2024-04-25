package examples.externalReferences.targeted.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class OneOfOne(
  @param:JsonProperty("oneOfOne")
  @get:JsonProperty("oneOfOne")
  public val oneOfOne: String? = null,
  @get:JsonProperty("discriminator")
  @get:NotNull
  @param:JsonProperty("discriminator")
  override val discriminator: String = "OneOfOne",
) : ParentOneOf()
