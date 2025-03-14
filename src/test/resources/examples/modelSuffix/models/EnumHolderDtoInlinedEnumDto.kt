package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class EnumHolderDtoInlinedEnumDto(
  @JsonValue
  public val `value`: String,
) {
  INLINED_ONE("inlined_one"),
  INLINED_TWO("inlined_two"),
  INLINED_THREE("inlined_three"),
  ;

  public companion object {
    private val mapping: Map<String, EnumHolderDtoInlinedEnumDto> =
        entries.associateBy(EnumHolderDtoInlinedEnumDto::value)

    public fun fromValue(`value`: String): EnumHolderDtoInlinedEnumDto? = mapping[value]
  }
}
