package examples.instantDateTime.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.collections.List

public data class QueryResult(
  @param:JsonProperty("items")
  @get:JsonProperty("items")
  @get:NotNull
  @get:Size(min = 0)
  @get:Valid
  public val items: List<FirstModel>,
)
