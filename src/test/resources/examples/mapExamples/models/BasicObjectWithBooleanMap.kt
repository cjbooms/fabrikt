package examples.mapExamples.models

import com.fasterxml.jackson.`annotation`.JsonAnyGetter
import com.fasterxml.jackson.`annotation`.JsonAnySetter
import com.fasterxml.jackson.`annotation`.JsonIgnore
import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Boolean
import kotlin.String
import kotlin.collections.Map
import kotlin.collections.MutableMap

public data class BasicObjectWithBooleanMap(
  @param:JsonProperty("one")
  @get:JsonProperty("one")
  public val one: String? = null,
  @get:JsonIgnore
  public val properties: MutableMap<String, Boolean?> = mutableMapOf(),
) {
  @JsonAnyGetter
  public fun `get`(): Map<String, Boolean?> = properties

  @JsonAnySetter
  public fun `set`(name: String, `value`: Boolean?) {
    properties[name] = value
  }
}
