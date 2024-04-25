package examples.oneOfPolymorphicModels.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.String

public data class PolymorphicTypeOneA(
  @param:JsonProperty("whateverA")
  @get:JsonProperty("whateverA")
  public val whateverA: String? = null,
  @get:JsonProperty("shared")
  @get:NotNull
  @param:JsonProperty("shared")
  override val shared: String = "PolymorphicTypeOneA",
) : PolymorphicSuperTypeOne()
