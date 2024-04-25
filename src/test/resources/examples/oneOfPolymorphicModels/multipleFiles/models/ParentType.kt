package examples.oneOfPolymorphicModels.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class ParentType(
  @JsonValue
  public val `value`: String,
) {
  CHILD_TYPE_A("CHILD_TYPE_A"),
  CHILD_TYPE_B("CHILD_TYPE_B"),
  ;

  public companion object {
    private val mapping: Map<String, ParentType> = values().associateBy(ParentType::value)

    public fun fromValue(`value`: String): ParentType? = mapping[value]
  }
}
