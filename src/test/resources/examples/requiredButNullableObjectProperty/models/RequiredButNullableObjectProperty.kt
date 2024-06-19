package examples.requiredButNullableObjectProperty.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public data class RequiredButNullableObjectProperty(
  @param:JsonProperty("code")
  @get:JsonProperty("code")
  @get:NotNull
  @get:Size(
    min = 1,
    max = 64,
  )
  public val code: String,
  @param:JsonProperty("summary")
  @get:JsonProperty("summary")
  @get:NotNull
  @get:Size(min = 1)
  public val summary: String,
  @param:JsonProperty("details")
  @get:JsonProperty("details")
  public val details: Map<String, Any>?,
)
