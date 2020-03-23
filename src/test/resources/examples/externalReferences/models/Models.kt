package examples.externalReferences.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import kotlin.String

data class ContainingExternalReference(
    @param:JsonProperty("some-external-reference")
    @get:JsonProperty("some-external-reference")
    @get:Valid
    val someExternalReference: ExternalObject? = null
)

data class ExternalObject(
    @param:JsonProperty("some-string")
    @get:JsonProperty("some-string")
    val someString: String? = null
)
