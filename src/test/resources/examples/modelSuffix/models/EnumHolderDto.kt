package examples.modelSuffix.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.String
import kotlin.collections.List

public data class EnumHolderDto(
  @param:JsonProperty("array_of_enums")
  @get:JsonProperty("array_of_enums")
  public val arrayOfEnums: List<String>? = null,
  @param:JsonProperty("inlined_enum")
  @get:JsonProperty("inlined_enum")
  public val inlinedEnum: EnumHolderDtoInlinedEnumDto? = null,
  @param:JsonProperty("inlined_extensible_enum")
  @get:JsonProperty("inlined_extensible_enum")
  public val inlinedExtensibleEnum: String? = null,
  @param:JsonProperty("enum_ref")
  @get:JsonProperty("enum_ref")
  public val enumRef: EnumObjectDto? = null,
  @param:JsonProperty("list_enums")
  @get:JsonProperty("list_enums")
  public val listEnums: List<String>? = null,
)
