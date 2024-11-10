package examples.githubApi.models

import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PullRequest(
  @SerialName("id")
  public val id: String? = null,
  @SerialName("audit_actor")
  public val auditActor: String? = null,
  @SerialName("created")
  public val created: Instant? = null,
  @SerialName("created_by")
  public val createdBy: String? = null,
  @SerialName("created_by_uid")
  public val createdByUid: String? = null,
  @SerialName("modified")
  public val modified: Instant? = null,
  @SerialName("modified_by")
  public val modifiedBy: String? = null,
  @SerialName("modified_by_uid")
  public val modifiedByUid: String? = null,
  @SerialName("status")
  @get:NotNull
  public val status: PullRequestStatus,
  @SerialName("etag")
  public val etag: String? = null,
  @SerialName("title")
  @get:NotNull
  public val title: String,
  @SerialName("description")
  public val description: String? = null,
  @SerialName("author")
  @get:Valid
  public val author: Author? = null,
)
