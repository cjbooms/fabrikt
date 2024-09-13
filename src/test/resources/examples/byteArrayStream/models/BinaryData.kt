package examples.byteArrayStream.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import java.io.InputStream

public data class BinaryData(
  @param:JsonProperty("binaryValue")
  @get:JsonProperty("binaryValue")
  public val binaryValue: InputStream? = null,
)
