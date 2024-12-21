package examples.internalVisibility.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

internal enum class Status(
  @JsonValue
  internal val `value`: String,
) {
  A("a"),
  B("b"),
  ;

  internal companion object {
    private val mapping: Map<String, Status> = entries.associateBy(Status::value)

    internal fun fromValue(`value`: String): Status? = mapping[value]
  }
}
