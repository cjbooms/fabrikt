package examples.openapi310.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.Any
import kotlin.String
import kotlin.collections.List

public data class NewNullableFormat(
  @param:JsonProperty("simpleNullable")
  @get:JsonProperty("simpleNullable")
  public val simpleNullable: String?,
  @param:JsonProperty("objectNullable")
  @get:JsonProperty("objectNullable")
  @get:Valid
  public val objectNullable: OneObject? = null,
  @param:JsonProperty("complexNullable")
  @get:JsonProperty("complexNullable")
  public val complexNullable: List<Any>? = null,
)
