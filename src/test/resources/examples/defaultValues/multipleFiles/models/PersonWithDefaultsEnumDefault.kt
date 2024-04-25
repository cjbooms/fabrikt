package examples.defaultValues.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class PersonWithDefaultsEnumDefault(
  @JsonValue
  public val `value`: String,
) {
  TALL("tall"),
  SHORT("short"),
  ;

  public companion object {
    private val mapping: Map<String, PersonWithDefaultsEnumDefault> =
        values().associateBy(PersonWithDefaultsEnumDefault::value)

    public fun fromValue(`value`: String): PersonWithDefaultsEnumDefault? = mapping[value]
  }
}
