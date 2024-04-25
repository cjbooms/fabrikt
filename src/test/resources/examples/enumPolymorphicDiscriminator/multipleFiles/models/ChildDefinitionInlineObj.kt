package examples.enumPolymorphicDiscriminator.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ChildDefinitionInlineObj(
  @param:JsonProperty("str")
  @get:JsonProperty("str")
  public val str: String? = null,
)
