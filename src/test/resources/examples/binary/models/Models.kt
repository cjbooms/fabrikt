package examples.binary.models

import com.fasterxml.jackson.`annotation`.JsonProperty
import kotlin.ByteArray

public data class BinaryData(
    @param:JsonProperty("binaryValue")
    @get:JsonProperty("binaryValue")
    public val binaryValue: ByteArray? = null,
)
