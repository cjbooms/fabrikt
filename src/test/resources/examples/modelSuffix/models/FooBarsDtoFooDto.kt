package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class FooBarsDtoFooDto(
  @JsonValue
  public val `value`: String,
) {
  X("X"),
  Y("Y"),
  ;

  public companion object {
    private val mapping: Map<String, FooBarsDtoFooDto> =
        entries.associateBy(FooBarsDtoFooDto::value)

    public fun fromValue(`value`: String): FooBarsDtoFooDto? = mapping[value]
  }
}
