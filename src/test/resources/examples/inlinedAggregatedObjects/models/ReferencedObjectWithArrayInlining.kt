package examples.inlinedAggregatedObjects.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.collections.List

public data class ReferencedObjectWithArrayInlining(
  @param:JsonProperty("annotations")
  @get:JsonProperty("annotations")
  @get:Valid
  public val annotations: List<ReferencedObjectWithArrayInliningAnnotations>? = null,
)
