
package authenticationTest.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import kotlin.Boolean

data class TestDTO(
    @param:JsonProperty("unread")
    @get:JsonProperty("unread")
    @get:NotNull
    val unread: Boolean
)