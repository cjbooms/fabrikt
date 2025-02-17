package com.cjbooms.fabrikt.models.kotlinx.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.net.URI

/**
 * Custom serializer for [URI] that serializes the value as a string.
 */
object URIAsStringSerializer : KSerializer<URI> {
    override val descriptor = PrimitiveSerialDescriptor(
        "java.net.URI", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): URI {
        return URI.create(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: URI) {
        encoder.encodeString(value.toString())
    }
}