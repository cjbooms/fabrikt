package com.cjbooms.fabrikt.models.kotlinx.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import java.math.BigDecimal

/**
 * Custom serializer for [BigDecimal] that serializes the value as a string without quotes.
 *
 * https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/json.md#serializing-large-decimal-numbers
 */
object BigDecimalSerializer : KSerializer<BigDecimal> {
    override val descriptor = PrimitiveSerialDescriptor(
        "java.math.BigDecimal", PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): BigDecimal {
        return if (decoder is JsonDecoder) {
            BigDecimal(decoder.decodeJsonElement().jsonPrimitive.content)
        } else {
            BigDecimal(decoder.decodeString())
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: BigDecimal) {
        val bigDecimalString = value.toPlainString()

        if (encoder is JsonEncoder) {
            encoder.encodeJsonElement(JsonUnquotedLiteral(bigDecimalString))
        } else {
            encoder.encodeString(bigDecimalString)
        }
    }
}