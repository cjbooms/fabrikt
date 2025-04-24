package examples.inlinedAggregatedObjects.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.String
import kotlin.collections.List

public data class ContainerArrayWithAllOfAggregationOfMany(
  @param:JsonProperty("annotations")
  @get:JsonProperty("annotations")
  @get:Valid
  public val annotations: List<ContainerAnnotations>? = null,
  @param:JsonProperty("id")
  @get:JsonProperty("id")
  public val id: String? = null,
)
