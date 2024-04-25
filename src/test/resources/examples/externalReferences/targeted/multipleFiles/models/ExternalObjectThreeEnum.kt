package examples.externalReferences.targeted.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class ExternalObjectThreeEnum(
  @JsonValue
  public val `value`: String,
) {
  ONE("one"),
  TWO("two"),
  THREE("three"),
  ;

  public companion object {
    private val mapping: Map<String, ExternalObjectThreeEnum> =
        values().associateBy(ExternalObjectThreeEnum::value)

    public fun fromValue(`value`: String): ExternalObjectThreeEnum? = mapping[value]
  }
}
