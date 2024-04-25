package examples.discriminatedOneOf.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull

public data class SomeObj(
  @param:JsonProperty("state")
  @get:JsonProperty("state")
  @get:NotNull
  @get:Valid
  public val state: State,
)
