package examples.enumExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class ExtensibleEnumObject(
  @JsonValue
  public val `value`: String,
) {
  ACTIVE("active"),
  INACTIVE("inactive"),
  ;

  public companion object {
    private val mapping: Map<String, ExtensibleEnumObject> =
        values().associateBy(ExtensibleEnumObject::value)

    public fun fromValue(`value`: String): ExtensibleEnumObject? = mapping[value]
  }
}
