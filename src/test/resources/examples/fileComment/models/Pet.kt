//
// This file was generated from an OpenAPI specification by Fabrikt.
// DO NOT EDIT. Changes will be lost the next time the code is regenerated.
// Instead, update the spec and regenerate to update.
//
package examples.fileComment.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.String

public data class Pet(
  @param:JsonProperty("id")
  @get:JsonProperty("id")
  public val id: Long? = null,
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  @get:NotNull
  public val name: String,
  @param:JsonProperty("tag")
  @get:JsonProperty("tag")
  @get:NotNull
  public val tag: String,
  @param:JsonProperty("type")
  @get:JsonProperty("type")
  public val type: PetType? = null,
)
