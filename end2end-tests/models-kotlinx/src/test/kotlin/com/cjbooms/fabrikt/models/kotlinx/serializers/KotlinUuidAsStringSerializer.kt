package com.cjbooms.fabrikt.models.kotlinx.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Custom serializer for [Uuid] that serializes the value as a string.
 */
@OptIn(ExperimentalUuidApi::class)
object KotlinUuidAsStringSerializer : KSerializer<Uuid> {
    override val descriptor = PrimitiveSerialDescriptor(
        "kotlin.uuid.Uuid", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Uuid {
        return Uuid.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uuid) {
        encoder.encodeString(value.toString())
    }
}