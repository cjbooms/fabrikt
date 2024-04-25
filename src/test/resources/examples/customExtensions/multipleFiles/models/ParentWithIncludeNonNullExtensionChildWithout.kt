package examples.customExtensions.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ParentWithIncludeNonNullExtensionChildWithout(
  @param:JsonProperty("direct")
  @get:JsonProperty("direct")
  public val direct: String? = null,
)
