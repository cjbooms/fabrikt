package examples.customExtensions.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import org.openapitools.jackson.nullable.JsonNullable

public data class TopLevelLevelMergePatchRef(
  @param:JsonProperty("inner")
  @get:JsonProperty("inner")
  @get:Valid
  public val `inner`: JsonNullable<InnerNotMergePatch> = JsonNullable.undefined(),
)
