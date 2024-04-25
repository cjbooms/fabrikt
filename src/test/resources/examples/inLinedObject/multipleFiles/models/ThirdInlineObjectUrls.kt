package examples.inLinedObject.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class ThirdInlineObjectUrls(
  @param:JsonProperty("version")
  @get:JsonProperty("version")
  public val version: String? = null,
)
