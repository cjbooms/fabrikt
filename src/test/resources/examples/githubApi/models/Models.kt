package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonAnyGetter
import com.fasterxml.jackson.`annotation`.JsonAnySetter
import com.fasterxml.jackson.`annotation`.JsonIgnore
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonValue
import java.net.URI
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size
import kotlin.Any
import kotlin.String
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableMap

public data class Author(
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    public val name: String? = null,
    @param:JsonProperty("email")
    @get:JsonProperty("email")
    public val email: String? = null,
)

public data class BulkEntityDetails(
    @param:JsonProperty("entities")
    @get:JsonProperty("entities")
    @get:NotNull
    @get:Valid
    public val entities: List<EntityDetails>,
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class Contributor(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    public val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    public val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    public val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    public val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    public val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    public val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    public val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    public val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    public val status: ContributorStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    public val etag: String? = null,
    @param:JsonProperty("username")
    @get:JsonProperty("username")
    @get:NotNull
    public val username: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    public val name: String? = null,
)

public data class ContributorQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    public val prev: URI? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    public val next: URI? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<Contributor>,
)

public enum class ContributorStatus(
    @JsonValue
    public val `value`: String,
) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    public companion object {
        private val mapping: Map<String, ContributorStatus> =
            values().associateBy(ContributorStatus::value)

        public fun fromValue(`value`: String): ContributorStatus? = mapping[value]
    }
}

public data class EntityDetails(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    @get:NotNull
    public val id: String,
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class Event(
    @param:JsonProperty("entity_id")
    @get:JsonProperty("entity_id")
    @get:NotNull
    public val entityId: String,
    @param:JsonProperty("data")
    @get:JsonProperty("data")
    @get:NotNull
    public val `data`: Map<String, Any>,
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class EventResults(
    @param:JsonProperty("change_events")
    @get:JsonProperty("change_events")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val changeEvents: List<Event>,
) {
    @get:JsonIgnore
    public val properties: MutableMap<String, Any> = mutableMapOf()

    @JsonAnyGetter
    public fun `get`(): Map<String, Any> = properties

    @JsonAnySetter
    public fun `set`(name: String, `value`: Any) {
        properties[name] = value
    }
}

public data class Organisation(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    public val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    public val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    public val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    public val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    public val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    public val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    public val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    public val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    public val status: OrganisationStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    public val etag: String? = null,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
    @param:JsonProperty("icon")
    @get:JsonProperty("icon")
    public val icon: String? = null,
    @param:JsonProperty("hooks")
    @get:JsonProperty("hooks")
    @get:Valid
    public val hooks: List<Webhook>? = null,
)

public data class OrganisationQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    public val prev: URI? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    public val next: URI? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<Organisation>,
)

public enum class OrganisationStatus(
    @JsonValue
    public val `value`: String,
) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    public companion object {
        private val mapping: Map<String, OrganisationStatus> =
            values().associateBy(OrganisationStatus::value)

        public fun fromValue(`value`: String): OrganisationStatus? = mapping[value]
    }
}

public data class PullRequest(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    public val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    public val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    public val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    public val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    public val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    public val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    public val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    public val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    public val status: PullRequestStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    public val etag: String? = null,
    @param:JsonProperty("title")
    @get:JsonProperty("title")
    @get:NotNull
    public val title: String,
    @param:JsonProperty("description")
    @get:JsonProperty("description")
    public val description: String? = null,
    @param:JsonProperty("author")
    @get:JsonProperty("author")
    @get:Valid
    public val author: Author? = null,
)

public data class PullRequestQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    public val prev: URI? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    public val next: URI? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<PullRequest>,
)

public enum class PullRequestStatus(
    @JsonValue
    public val `value`: String,
) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    public companion object {
        private val mapping: Map<String, PullRequestStatus> =
            values().associateBy(PullRequestStatus::value)

        public fun fromValue(`value`: String): PullRequestStatus? = mapping[value]
    }
}

public data class Repository(
    @param:JsonProperty("id")
    @get:JsonProperty("id")
    public val id: String? = null,
    @param:JsonProperty("audit_actor")
    @get:JsonProperty("audit_actor")
    public val auditActor: String? = null,
    @param:JsonProperty("created")
    @get:JsonProperty("created")
    public val created: OffsetDateTime? = null,
    @param:JsonProperty("created_by")
    @get:JsonProperty("created_by")
    public val createdBy: String? = null,
    @param:JsonProperty("created_by_uid")
    @get:JsonProperty("created_by_uid")
    public val createdByUid: String? = null,
    @param:JsonProperty("modified")
    @get:JsonProperty("modified")
    public val modified: OffsetDateTime? = null,
    @param:JsonProperty("modified_by")
    @get:JsonProperty("modified_by")
    public val modifiedBy: String? = null,
    @param:JsonProperty("modified_by_uid")
    @get:JsonProperty("modified_by_uid")
    public val modifiedByUid: String? = null,
    @param:JsonProperty("status")
    @get:JsonProperty("status")
    @get:NotNull
    public val status: RepositoryStatus,
    @param:JsonProperty("etag")
    @get:JsonProperty("etag")
    public val etag: String? = null,
    @param:JsonProperty("slug")
    @get:JsonProperty("slug")
    @get:NotNull
    public val slug: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    @get:NotNull
    public val name: String,
    @param:JsonProperty("visibility")
    @get:JsonProperty("visibility")
    public val visibility: RepositoryVisibility? = null,
    @param:JsonProperty("tags")
    @get:JsonProperty("tags")
    public val tags: List<String>? = null,
)

public data class RepositoryQueryResult(
    @param:JsonProperty("prev")
    @get:JsonProperty("prev")
    public val prev: URI? = null,
    @param:JsonProperty("next")
    @get:JsonProperty("next")
    public val next: URI? = null,
    @param:JsonProperty("items")
    @get:JsonProperty("items")
    @get:NotNull
    @get:Size(min = 0)
    @get:Valid
    public val items: List<Repository>,
)

public enum class RepositoryStatus(
    @JsonValue
    public val `value`: String,
) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ;

    public companion object {
        private val mapping: Map<String, RepositoryStatus> =
            values().associateBy(RepositoryStatus::value)

        public fun fromValue(`value`: String): RepositoryStatus? = mapping[value]
    }
}

public enum class RepositoryVisibility(
    @JsonValue
    public val `value`: String,
) {
    PRIVATE("Private"),
    PUBLIC("Public"),
    ;

    public companion object {
        private val mapping: Map<String, RepositoryVisibility> =
            values().associateBy(RepositoryVisibility::value)

        public fun fromValue(`value`: String): RepositoryVisibility? = mapping[value]
    }
}

public enum class StatusQueryParam(
    @JsonValue
    public val `value`: String,
) {
    ACTIVE("active"),
    INACTIVE("inactive"),
    ALL("all"),
    ;

    public companion object {
        private val mapping: Map<String, StatusQueryParam> =
            values().associateBy(StatusQueryParam::value)

        public fun fromValue(`value`: String): StatusQueryParam? = mapping[value]
    }
}

public data class Webhook(
    @param:JsonProperty("url")
    @get:JsonProperty("url")
    @get:NotNull
    public val url: String,
    @param:JsonProperty("name")
    @get:JsonProperty("name")
    public val name: String? = null,
)
