package com.cjbooms.fabrikt.models.kotlinx

import com.example.primitives.models.Content
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

class KotlinxSerializationPrimitiveTypesTest {

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

    private val jsonWithCustomSerializers = Json {
        serializersModule = SerializersModule {
            // register contextual custom serializers
            contextual(BigDecimalSerializer)
        }
        prettyPrint = true
    }

    @Test
    fun `must serialize Content`() {
        val content = Content(
            integer = 1,
            integer32 = 2147483647,
            integer64 = 9223372036854775807,
            boolean = true,
            string = "example",
            stringUuid = "123e4567-e89b-12d3-a456-426614174000",
            stringUri = "https://example.org",
            stringDate = LocalDate.parse("2020-02-04"),
            stringDateTime = Instant.parse("2024-11-04T12:00:00Z"),
            number = BigDecimal("109288282772724.4225837838838383888"),
            numberFloat = 1.23f,
            numberDouble = 4.56,
        )

        val result = jsonWithCustomSerializers.encodeToString(content)

        val expected = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize Content`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        val content: Content = jsonWithCustomSerializers.decodeFromString(jsonString)

        val expectedContent = Content(
            integer = 1,
            integer32 = 2147483647,
            integer64 = 9223372036854775807,
            boolean = true,
            string = "example",
            stringUuid = "123e4567-e89b-12d3-a456-426614174000",
            stringUri = "https://example.org",
            stringDate = LocalDate.parse("2020-02-04"),
            stringDateTime = Instant.parse("2024-11-04T12:00:00Z"),
            number = BigDecimal("109288282772724.4225837838838383888"),
            numberFloat = 1.23f,
            numberDouble = 4.56,
        )

        assertThat(content.integer).isEqualTo(expectedContent.integer)
        assertThat(content.integer32).isEqualTo(expectedContent.integer32)
        assertThat(content.integer64).isEqualTo(expectedContent.integer64)
        assertThat(content.boolean).isEqualTo(expectedContent.boolean)
        assertThat(content.string).isEqualTo(expectedContent.string)
        assertThat(content.stringUuid).isEqualTo(expectedContent.stringUuid)
        assertThat(content.stringUri).isEqualTo(expectedContent.stringUri)
        assertThat(content.stringDate).isEqualTo(expectedContent.stringDate)
        assertThat(content.stringDateTime).isEqualTo(expectedContent.stringDateTime)
        assertThat(content.number).isEqualTo(expectedContent.number)
        assertThat(content.numberFloat).isEqualTo(expectedContent.numberFloat)
        assertThat(content.numberDouble).isEqualTo(expectedContent.numberDouble)
    }

    @Test
    fun `fails to deserialize if custom serializer is not found`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        val e = assertThrows<SerializationException> {
            Json.decodeFromString<Content>(jsonString)
        }

        assertThat(e.message).contains("Serializer for class 'BigDecimal' is not found.")
    }
}