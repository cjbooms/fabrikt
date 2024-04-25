package examples.customExtensions.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonInclude
import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

public data class ParentWithIncludeNonNullExtension(
  @param:JsonProperty("simpleNullable")
  @get:JsonProperty("simpleNullable")
  @param:JsonInclude(JsonInclude.Include.NON_NULL)
  public val simpleNullable: String? = null,
  @param:JsonProperty("simpleRequired")
  @get:JsonProperty("simpleRequired")
  @get:NotNull
  public val simpleRequired: String,
  @param:JsonProperty("childWithout")
  @get:JsonProperty("childWithout")
  @get:Valid
  @param:JsonInclude(JsonInclude.Include.NON_NULL)
  public val childWithout: ParentWithIncludeNonNullExtensionChildWithout? = null,
)
