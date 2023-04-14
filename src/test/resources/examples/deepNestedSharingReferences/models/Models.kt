package examples.deepNestedSharingReferences.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import kotlin.String

data class Address(
    @param:JsonProperty("eircode")
    @get:JsonProperty("eircode")
    val eircode: String? = null,
)

data class Company(
    @param:JsonProperty("owner")
    @get:JsonProperty("owner")
    @get:Valid
    val owner: Person? = null,
    @param:JsonProperty("employee")
    @get:JsonProperty("employee")
    @get:Valid
    val employee: Person? = null,
)

data class Department(
    @param:JsonProperty("supervisor")
    @get:JsonProperty("supervisor")
    @get:Valid
    val supervisor: Person? = null,
    @param:JsonProperty("manager")
    @get:JsonProperty("manager")
    @get:Valid
    val manager: Person? = null,
)

data class Person(
    @param:JsonProperty("home_address")
    @get:JsonProperty("home_address")
    @get:Valid
    val homeAddress: Address? = null,
)
