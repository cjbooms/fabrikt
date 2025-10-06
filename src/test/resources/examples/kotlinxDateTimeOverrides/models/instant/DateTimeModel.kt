package examples.kotlinxDateTimeOverrides.models

import java.time.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DateTimeModel(
  @Contextual
  @SerialName("createdAt")
  public val createdAt: Instant? = null,
  @Contextual
  @SerialName("updatedAt")
  public val updatedAt: Instant? = null,
)
