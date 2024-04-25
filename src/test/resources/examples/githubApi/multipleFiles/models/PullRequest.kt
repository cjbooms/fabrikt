package examples.githubApi.multipleFiles.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.time.OffsetDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String

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
