package examples.duplicatePropertyHandling.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class DuplicatesParent(
  @param:JsonProperty("child_duplicate")
  @get:JsonProperty("child_duplicate")
  public val childDuplicate: String? = null,
  @param:JsonProperty("top_level_duplicate")
  @get:JsonProperty("top_level_duplicate")
  public val topLevelDuplicate: String? = null,
)
