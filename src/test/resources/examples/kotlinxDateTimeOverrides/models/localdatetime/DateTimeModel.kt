package examples.kotlinxDateTimeOverrides.models

import java.time.LocalDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DateTimeModel(
  @Contextual
  @SerialName("createdAt")
  public val createdAt: LocalDateTime? = null,
  @Contextual
  @SerialName("updatedAt")
  public val updatedAt: LocalDateTime? = null,
)
