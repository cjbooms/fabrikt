package examples.deepNestedSharingReferences.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import kotlin.String

public data class Address(
    @param:JsonProperty("eircode")
    @get:JsonProperty("eircode")
    public val eircode: String? = null,
)

public data class Company(
    @param:JsonProperty("owner")
    @get:JsonProperty("owner")
    @get:Valid
    public val owner: Person? = null,
    @param:JsonProperty("employee")
    @get:JsonProperty("employee")
    @get:Valid
    public val employee: Person? = null,
)

public data class Department(
    @param:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    @get:Valid
    public val supervisor: Person? = null,
    @param:JsonProperty("manager")
    @get:JsonProperty("manager")
    @get:Valid
    public val manager: Person? = null,
)

public data class Person(
    @param:JsonProperty("home_address")
    @get:JsonProperty("home_address")
    @get:Valid
    public val homeAddress: Address? = null,
)
