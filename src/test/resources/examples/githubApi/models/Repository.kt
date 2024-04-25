package examples.githubApi.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.OffsetDateTime
import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List

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
