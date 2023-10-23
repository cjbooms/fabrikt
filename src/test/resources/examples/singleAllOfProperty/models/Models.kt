package examples.singleAllOfProperty.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

public data class Dog(
    @param:JsonProperty("owner")
    @get:JsonProperty("owner")
    @get:Valid
    public val owner: Person? = null,
    @param:JsonProperty("walker")
    @get:JsonProperty("walker")
    @get:Valid
    public val walker: Person? = null,
)

public data class Person(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
)
