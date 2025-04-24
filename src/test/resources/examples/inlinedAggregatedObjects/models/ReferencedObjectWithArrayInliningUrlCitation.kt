package examples.inlinedAggregatedObjects.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int

public data class ReferencedObjectWithArrayInliningUrlCitation(
  @param:JsonProperty("end_index")
  @get:JsonProperty("end_index")
  public val endIndex: Int? = null,
)
