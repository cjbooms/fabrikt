package examples.customExtensions.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid

public data class InnerOnlyMergePatchInline(
  @param:JsonProperty("inner")
  @get:JsonProperty("inner")
  @get:Valid
  public val `inner`: InnerOnlyMergePatchInlineInner? = null,
)
