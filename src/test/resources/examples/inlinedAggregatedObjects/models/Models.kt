package examples.inlinedAggregatedObjects.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

public data class Company(
    @param:JsonProperty("companyName")
    @get:JsonProperty("companyName")
    @get:NotNull
    public val companyName: String,
)

public data class Dog(
    @param:JsonProperty("owner")
    @get:JsonProperty("owner")
    @get:Valid
    public val owner: Person? = null,
    @param:JsonProperty("walker")
    @get:JsonProperty("walker")
    @get:Valid
    public val walker: DogWalker? = null,
)

public data class DogWalker(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
    @param:JsonProperty("companyName")
    @get:JsonProperty("companyName")
    @get:NotNull
    public val companyName: String,
)

public data class Person(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
)
