package examples.customExtensions.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import org.openapitools.jackson.nullable.JsonNullable

public data class InnerOnlyMergePatchInlineInner(
  @param:JsonProperty("p")
  @get:JsonProperty("p")
  public val p: JsonNullable<String> = JsonNullable.undefined(),
)
