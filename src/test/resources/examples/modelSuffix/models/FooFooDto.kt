package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class FooFooDto(
  @JsonValue
  public val `value`: String,
) {
  X("X"),
  Y("Y"),
  ;

  public companion object {
    private val mapping: Map<String, FooFooDto> = values().associateBy(FooFooDto::value)

    public fun fromValue(`value`: String): FooFooDto? = mapping[value]
  }
}
