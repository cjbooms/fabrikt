package examples.customExtensions.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class NoMergePatchInline(
  @param:JsonProperty("inner")
  @get:JsonProperty("inner")
  @get:Valid
  public val `inner`: NoMergePatchInlineInner? = null,
)
