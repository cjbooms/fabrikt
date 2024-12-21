package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

internal enum class StateBMode(
  @JsonValue
  internal val `value`: String,
) {
  MODE1("mode1"),
  MODE2("mode2"),
  ;

  internal companion object {
    private val mapping: Map<String, StateBMode> = entries.associateBy(StateBMode::value)

    internal fun fromValue(`value`: String): StateBMode? = mapping[value]
  }
}
