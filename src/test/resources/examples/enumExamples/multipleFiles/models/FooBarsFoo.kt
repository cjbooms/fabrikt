package examples.enumExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class FooBarsFoo(
  @JsonValue
  public val `value`: String,
) {
  X("X"),
  Y("Y"),
  ;

  public companion object {
    private val mapping: Map<String, FooBarsFoo> = values().associateBy(FooBarsFoo::value)

    public fun fromValue(`value`: String): FooBarsFoo? = mapping[value]
  }
}
