package examples.githubApi.models

import javax.validation.constraints.NotNull
import kotlin.String
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Contributor(
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
  public val status: ContributorStatus,
  @SerialName("etag")
  public val etag: String? = null,
  @SerialName("username")
  @get:NotNull
  public val username: String,
  @SerialName("name")
  public val name: String? = null,
)
