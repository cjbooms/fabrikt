package examples.enumPolymorphicDiscriminator.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.collections.List

public data class Responses(
  @param:JsonProperty("entries")
  @get:JsonProperty("entries")
  @get:Valid
  public val entries: List<ChildDefinition>? = null,
)
