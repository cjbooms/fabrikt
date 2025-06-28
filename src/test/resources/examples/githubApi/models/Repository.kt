package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

public data class Repository(
  /**
   * This unique server-generated identity will always be present in the response.
   */
  @param:JsonProperty("id")
  @get:JsonProperty("id")
  public val id: String? = null,
  /**
   * The actor authoring a write to a resource
   */
  @param:JsonProperty("audit_actor")
  @get:JsonProperty("audit_actor")
  public val auditActor: String? = null,
  /**
   * Indicates the timestamp that this resource was initially created at
   */
  @param:JsonProperty("created")
  @get:JsonProperty("created")
  public val created: OffsetDateTime? = null,
  /**
   * Represents the actor/user who first created this resource
   */
  @param:JsonProperty("created_by")
  @get:JsonProperty("created_by")
  public val createdBy: String? = null,
  /**
   * Represents the uid from the auth token that first created this resource
   */
  @param:JsonProperty("created_by_uid")
  @get:JsonProperty("created_by_uid")
  public val createdByUid: String? = null,
  /**
   * Indicates the timestamp that this resource was last modified at
   */
  @param:JsonProperty("modified")
  @get:JsonProperty("modified")
  public val modified: OffsetDateTime? = null,
  /**
   * Represents the actor/user who last modified this resource
   */
  @param:JsonProperty("modified_by")
  @get:JsonProperty("modified_by")
  public val modifiedBy: String? = null,
  /**
   * Represents the uid from the auth token that last modified this resource
   */
  @param:JsonProperty("modified_by_uid")
  @get:JsonProperty("modified_by_uid")
  public val modifiedByUid: String? = null,
  /**
   * States whether the entity is currently active or inactive
   */
  @param:JsonProperty("status")
  @get:JsonProperty("status")
  @get:NotNull
  public val status: RepositoryStatus,
  /**
   * Server generated value which is used as a version for the resource. This value is to be used in
   * conjunction with If-Match headers for optimistic locking purposes
   */
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
