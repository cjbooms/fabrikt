package examples.defaultValues.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class PersonWithDefaultsEnumQuotedDefault(
  @JsonValue
  public val `value`: String,
) {
  `1X`("1x"),
  `2X`("2x"),
  ;

  public companion object {
    private val mapping: Map<String, PersonWithDefaultsEnumQuotedDefault> =
        values().associateBy(PersonWithDefaultsEnumQuotedDefault::value)

    public fun fromValue(`value`: String): PersonWithDefaultsEnumQuotedDefault? = mapping[value]
  }
}
