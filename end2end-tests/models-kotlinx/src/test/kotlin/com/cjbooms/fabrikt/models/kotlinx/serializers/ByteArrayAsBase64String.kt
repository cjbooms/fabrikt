package com.cjbooms.fabrikt.models.kotlinx.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Custom serializer for [ByteArray] that serializes the value as a base64 string.
 *
 * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#base64
 */
@OptIn(ExperimentalEncodingApi::class)
object ByteArrayAsBase64String : KSerializer<ByteArray> {
    private val base64 = Base64

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "ByteArrayAsBase64Serializer", PrimitiveKind.STRING
    )

    override fun serialize(encoder: Encoder, value: ByteArray) {
        val base64Encoded = base64.encode(value)
        encoder.encodeString(base64Encoded)
    }

    override fun deserialize(decoder: Decoder): ByteArray {
        val base64Decoded = decoder.decodeString()
        return base64.decode(base64Decoded)
    }
}