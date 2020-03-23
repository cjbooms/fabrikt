package examples.inLinedObject.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

data class ContainsInLinedObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    val generation: Generation? = null
)

data class CallHome(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    val url: String
)

data class DatabaseView(
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    @get:NotNull
    val viewName: String
)

data class Generation(
    @param:JsonProperty("call_home")
    @get:JsonProperty("call_home")
    @get:Valid
    val callHome: CallHome? = null,
    @param:JsonProperty("database_view")
    @get:JsonProperty("database_view")
    @get:Valid
    val databaseView: DatabaseView? = null,
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    val direct: String? = null
)
