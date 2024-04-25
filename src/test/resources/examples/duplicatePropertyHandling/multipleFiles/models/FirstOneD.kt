package examples.duplicatePropertyHandling.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class FirstOneD(
  @param:JsonProperty("child_duplicate")
  @get:JsonProperty("child_duplicate")
  public val childDuplicate: String? = null,
)
