package examples.inLinedObject.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

public data class FirstInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    public val generation: FirstInlineObjectGeneration? = null,
)

public data class FirstInlineObjectCallHome(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    public val url: String,
)

public data class FirstInlineObjectDatabaseView(
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    @get:NotNull
    public val viewName: String,
)

public data class FirstInlineObjectGeneration(
    @param:JsonProperty("call_home")
    @get:JsonProperty("call_home")
    @get:Valid
    public val callHome: FirstInlineObjectCallHome? = null,
    @param:JsonProperty("database_view")
    @get:JsonProperty("database_view")
    @get:Valid
    public val databaseView: FirstInlineObjectDatabaseView? = null,
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    public val direct: String? = null,
)

public data class SecondInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    public val generation: SecondInlineObjectGeneration? = null,
)

public data class SecondInlineObjectCallHome(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    public val url: String,
)

public data class SecondInlineObjectDatabaseView(
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    @get:NotNull
    public val viewName: String,
)

public data class SecondInlineObjectGeneration(
    @param:JsonProperty("call_home")
    @get:JsonProperty("call_home")
    @get:Valid
    public val callHome: SecondInlineObjectCallHome? = null,
    @param:JsonProperty("database_view")
    @get:JsonProperty("database_view")
    @get:Valid
    public val databaseView: SecondInlineObjectDatabaseView? = null,
    @param:JsonProperty("direct")
    @get:JsonProperty("direct")
    public val direct: String? = null,
)

public data class ThirdInlineObject(
    @param:JsonProperty("generation")
    @get:JsonProperty("generation")
    @get:Valid
    public val generation: ThirdInlineObjectGeneration? = null,
)

public data class ThirdInlineObjectGeneration(
    @param:JsonProperty("urls")
    @get:JsonProperty("urls")
    @get:Valid
    public val urls: List<ThirdInlineObjectUrls>? = null,
    @param:JsonProperty("view_name")
    @get:JsonProperty("view_name")
    public val viewName: String? = null,
)

public data class ThirdInlineObjectUrls(
    @param:JsonProperty("version")
    @get:JsonProperty("version")
    public val version: String? = null,
)
