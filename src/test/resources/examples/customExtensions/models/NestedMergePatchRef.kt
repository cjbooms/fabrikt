package examples.customExtensions.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import org.openapitools.jackson.nullable.JsonNullable

public data class NestedMergePatchRef(
  @param:JsonProperty("inner")
  @get:JsonProperty("inner")
  @get:Valid
  public val `inner`: JsonNullable<InnerMergePatch> = JsonNullable.undefined(),
)
