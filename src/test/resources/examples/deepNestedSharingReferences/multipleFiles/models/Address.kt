package examples.deepNestedSharingReferences.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String

public data class Address(
  @param:JsonProperty("eircode")
  @get:JsonProperty("eircode")
  public val eircode: String? = null,
)
