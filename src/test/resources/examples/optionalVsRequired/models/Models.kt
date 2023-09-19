package examples.optionalVsRequired.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.util.UUID
import javax.validation.constraints.NotNull
import kotlin.String

public data class OptionalVsRequired(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
    @param:JsonProperty("gender")
    @get:JsonProperty("gender")
    public val gender: UUID? = null,
)
