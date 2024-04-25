package examples.enumPolymorphicDiscriminator.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonValue
import kotlin.String
import kotlin.collections.Map

public enum class ChildDiscriminator(
  @JsonValue
  public val `value`: String,
) {
  OBJ_ONE_ONLY("obj_one_only"),
  OBJ_TWO_FIRST("obj_two_first"),
  OBJ_TWO_SECOND("obj_two_second"),
  OBJ_THREE("obj_three"),
  ;

  public companion object {
    private val mapping: Map<String, ChildDiscriminator> =
        values().associateBy(ChildDiscriminator::value)

    public fun fromValue(`value`: String): ChildDiscriminator? = mapping[value]
  }
}
