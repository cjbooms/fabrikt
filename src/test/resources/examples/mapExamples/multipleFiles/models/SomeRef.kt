package examples.mapExamples.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

public data class SomeRef(
  @param:JsonProperty("other_text")
  @get:JsonProperty("other_text")
  public val otherText: String? = null,
  @param:JsonProperty("other_number")
  @get:JsonProperty("other_number")
  public val otherNumber: Int? = null,
)
