package examples.enumExamples.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class FooFoo(
  @JsonValue
  public val `value`: String,
) {
  X("X"),
  Y("Y"),
  ;

  public companion object {
    private val mapping: Map<String, FooFoo> = values().associateBy(FooFoo::value)

    public fun fromValue(`value`: String): FooFoo? = mapping[value]
  }
}
