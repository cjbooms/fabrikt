package examples.uniqueItems.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.collections.Set

public data class UniqueArrayOfSomething(
  @get:NotNull
  @get:Valid
  public val quantities: Set<Something>,
)
