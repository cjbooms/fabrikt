package examples.githubApi.models

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap

data class BulkEntityDetails(
    @param:JsonProperty("entities")
    @get:JsonProperty("entities")
    @get:NotNull
    @get:Valid
    val entities: List<EntityDetails>
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Any> = properties

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class EntityDetails(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    @get:NotNull
    val id: String
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Any> = properties

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class EventResults(
    @param:JsonProperty("change_events")
    @get:JsonProperty("change_events")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val changeEvents: List<Event>
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Any> = properties

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class Event(
    @param:JsonProperty("entity_id")
    @get:JsonProperty("entity_id")
    @get:NotNull
    val entityId: String,
    @param:JsonProperty("data")
    @get:JsonProperty("data")
    @get:NotNull
    val data: Map<String, Any>
) {
    @get:JsonIgnore
    val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    fun get(): Map<String, Any> = properties

    @JsonAnySetter
    fun set(name: String, value: Any) {
        properties[name] = value
    }
}

data class ContributorQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<Contributor>
)

enum class ContributorStatus(
    @JsonValue
    val value: String
) {
    ACTIVE("active"),

    INACTIVE("inactive");
}

data class Contributor(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    val status: ContributorStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    val etag: String? = null,
    @param:JsonProperty("username")
    @get:JsonProperty("username")
    @get:NotNull
    val username: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    val name: String? = null
)

data class OrganisationQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<Organisation>
)

enum class OrganisationStatus(
    @JsonValue
    val value: String
) {
    ACTIVE("active"),

    INACTIVE("inactive");
}

data class Webhook(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    val url: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    val name: String? = null
)

data class Organisation(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    val status: OrganisationStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    val etag: String? = null,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("icon")
    @get:JsonProperty("icon")
    val icon: String? = null,
    @param:JsonProperty("hooks")
    @get:JsonProperty("hooks")
    @get:Valid
    val hooks: List<Webhook>? = null
)

data class RepositoryQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<Repository>
)

enum class RepositoryStatus(
    @JsonValue
    val value: String
) {
    ACTIVE("active"),

    INACTIVE("inactive");
}

enum class RepositoryVisibility(
    @JsonValue
    val value: String
) {
    PRIVATE("Private"),

    PUBLIC("Public");
}

data class Repository(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    val status: RepositoryStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    val etag: String? = null,
    @param:JsonProperty("slug")
    @get:JsonProperty("slug")
    @get:NotNull
    val slug: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    val name: String,
    @param:JsonProperty("visibility")
    @get:JsonProperty("visibility")
    val visibility: RepositoryVisibility? = null,
    @param:JsonProperty("tags")
    @get:JsonProperty("tags")
    val tags: List<String>? = null
)

data class PullRequestQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    val prev: String? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    val next: String? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    val items: List<PullRequest>
)

enum class PullRequestStatus(
    @JsonValue
    val value: String
) {
    ACTIVE("active"),

    INACTIVE("inactive");
}

data class Author(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    val name: String? = null,
    @param:JsonProperty("email")
    @get:JsonProperty("email")
    val email: String? = null
)

data class PullRequest(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    val status: PullRequestStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    val etag: String? = null,
    @param:JsonProperty("title")
    @get:JsonProperty("title")
    @get:NotNull
    val title: String,
    @param:JsonProperty("description")
    @get:JsonProperty("description")
    val description: String? = null,
    @param:JsonProperty("author")
    @get:JsonProperty("author")
    @get:Valid
    val author: Author? = null
)

enum class StatusQueryParam(
    @JsonValue
    val value: String
) {
    ACTIVE("active"),

    INACTIVE("inactive"),

    ALL("all");
}
