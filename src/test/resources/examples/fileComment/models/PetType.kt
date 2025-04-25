//
// This file was generated from an OpenAPI specification by Fabrikt.
// DO NOT EDIT. Changes will be lost the next time the code is regenerated.
// Instead, update the spec and regenerate to update.
//
package examples.fileComment.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class PetType(
  @JsonValue
  public val `value`: String,
) {
  LEGGED("legged"),
  WINGED("winged"),
  FINNED("finned"),
  ;

  public companion object {
    private val mapping: Map<String, PetType> = entries.associateBy(PetType::value)

    public fun fromValue(`value`: String): PetType? = mapping[value]
  }
}
