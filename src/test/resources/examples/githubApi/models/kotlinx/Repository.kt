package examples.githubApi.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlin.collections.List
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Repository(
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
  public val status: RepositoryStatus,
  @SerialName("etag")
  public val etag: String? = null,
  @SerialName("slug")
  @get:NotNull
  public val slug: String,
  @SerialName("name")
  @get:NotNull
  public val name: String,
  @SerialName("visibility")
  public val visibility: RepositoryVisibility? = null,
  @SerialName("tags")
  public val tags: List<String>? = null,
)
