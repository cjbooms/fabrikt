package examples.requiredReadOnly.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.String

public data class RequiredReadOnly(
    @param:JsonProperty("user_name")
    @get:JsonProperty("user_name")
    @get:NotNull
    public val userName: String,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    public val created: OffsetDateTime? = null,
)
