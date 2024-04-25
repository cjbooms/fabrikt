package examples.enumPolymorphicDiscriminator.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ChildDefinitionInlineArray(
  @param:JsonProperty("str")
  @get:JsonProperty("str")
  public val str: String? = null,
)
