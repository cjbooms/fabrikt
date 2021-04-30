package examples.inLinedObject.models

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

data class FirstInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    val generation: FirstInlineObjectGeneration? = null
)

data class FirstInlineObjectCallHome(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    val url: String
)

data class FirstInlineObjectDatabaseView(
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    @get:NotNull
    val viewName: String
)

data class FirstInlineObjectGeneration(
    @param:JsonProperty("call_home")
    @get:JsonProperty("call_home")
    @get:Valid
    val callHome: FirstInlineObjectCallHome? = null,
    @param:JsonProperty("database_view")
    @get:JsonProperty("database_view")
    @get:Valid
    val databaseView: FirstInlineObjectDatabaseView? = null,
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    val direct: String? = null
)

data class SecondInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    val generation: SecondInlineObjectGeneration? = null
)

data class SecondInlineObjectCallHome(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    val url: String
)

data class SecondInlineObjectDatabaseView(
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    @get:NotNull
    val viewName: String
)

data class SecondInlineObjectGeneration(
    @param:JsonProperty("call_home")
    @get:JsonProperty("call_home")
    @get:Valid
    val callHome: SecondInlineObjectCallHome? = null,
    @param:JsonProperty("database_view")
    @get:JsonProperty("database_view")
    @get:Valid
    val databaseView: SecondInlineObjectDatabaseView? = null,
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    val direct: String? = null
)

data class ThirdInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    val generation: ThirdInlineObjectGeneration? = null
)

data class ThirdInlineObjectUrls(
    @param:JsonProperty("version")
    @get:JsonProperty("version")
    val version: String? = null
)

data class ThirdInlineObjectGeneration(
    @param:JsonProperty("urls")
    @get:JsonProperty("urls")
    @get:Valid
    val urls: List<ThirdInlineObjectUrls>? = null,
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    val viewName: String? = null
)
