package examples.untypedObject.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public data class Result(
  /**
   * Any data. Object has no schema.
   */
  @param:JsonProperty("data")
  @get:JsonProperty("data")
  public val `data`: Map<String, Any?>? = null,
)
