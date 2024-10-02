package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class RootDiscriminatorDto(
  @JsonValue
  public val `value`: String,
) {
  FIRST_LEVEL_CHILD("firstLevelChild"),
  ;

  public companion object {
    private val mapping: Map<String, RootDiscriminatorDto> =
        values().associateBy(RootDiscriminatorDto::value)

    public fun fromValue(`value`: String): RootDiscriminatorDto? = mapping[value]
  }
}
