package examples.optionalVsRequired.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.util.UUID
import javax.validation.constraints.NotNull
import kotlin.Any
import kotlin.String
import kotlin.collections.Map

public data class OptionalVsRequired(
  @param:JsonProperty("name")
  @get:JsonProperty("name")
  @get:NotNull
  public val name: String,
  @param:JsonProperty("gender")
  @get:JsonProperty("gender")
  public val gender: UUID? = null,
  @param:JsonProperty("requiredNullableString")
  @get:JsonProperty("requiredNullableString")
  public val requiredNullableString: String?,
  @param:JsonProperty("requiredNullableUntypedObject")
  @get:JsonProperty("requiredNullableUntypedObject")
  public val requiredNullableUntypedObject: Map<String, Any?>?,
)
