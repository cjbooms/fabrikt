package examples.defaultValues.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.Int
import kotlin.String

public data class PersonWithDefaultsIgnoredObjectDefault(
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  public val name: String? = null,
  @param:JsonProperty("age")
  @get:JsonProperty("age")
  public val age: Int? = null,
)
