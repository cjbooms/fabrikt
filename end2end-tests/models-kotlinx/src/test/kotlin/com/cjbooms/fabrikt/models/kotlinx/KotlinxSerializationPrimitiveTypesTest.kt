package com.cjbooms.fabrikt.models.kotlinx

import com.cjbooms.fabrikt.models.kotlinx.serializers.BigDecimalSerializer
import com.cjbooms.fabrikt.models.kotlinx.serializers.ByteArrayAsBase64String
import com.cjbooms.fabrikt.models.kotlinx.serializers.KotlinUuidAsStringSerializer
import com.cjbooms.fabrikt.models.kotlinx.serializers.URIAsStringSerializer
import com.example.primitives.models.Content
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.net.URI
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * This test verifies that the generated Kotlinx serialization code for the primitive types uses the custom serializers
 * that are registered in the `Json` configuration.
 *
 * The custom serializers are resolved because the fields are annotated with `@Contextual`.
 */
@OptIn(ExperimentalUuidApi::class)
class KotlinxSerializationPrimitiveTypesTest {

    private val jsonWithCustomSerializers = Json {
        serializersModule = SerializersModule {
            // register contextual custom serializers
            contextual(BigDecimalSerializer)
            contextual(ByteArrayAsBase64String)
            contextual(KotlinUuidAsStringSerializer)
            contextual(URIAsStringSerializer)
        }
        prettyPrint = true
    }

    private val binFileContent = javaClass.getResource("/primitive_types/test.bin")!!.readBytes()

    private val content = Content(
        integer = 1,
        integer32 = 2147483647,
        integer64 = 9223372036854775807,
        boolean = true,
        string = "example",
        stringUuid = Uuid.parse("123e4567-e89b-12d3-a456-426614174000"),
        stringUri = URI.create("https://example.org"),
        stringDate = LocalDate.parse("2020-02-04"),
        stringDateTime = Instant.parse("2024-11-04T12:00:00Z"),
        number = BigDecimal("109288282772724.4225837838838383888"),
        numberFloat = 1.23f,
        numberDouble = 4.56,
        byte = binFileContent,
        binary = binFileContent
    )

    @Test
    fun `must serialize Content`() {
        val result = jsonWithCustomSerializers.encodeToString(content)

        val expected = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize Content`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        val obj: Content = jsonWithCustomSerializers.decodeFromString(jsonString)

        assertThat(obj.integer).isEqualTo(content.integer)
        assertThat(obj.integer32).isEqualTo(content.integer32)
        assertThat(obj.integer64).isEqualTo(content.integer64)
        assertThat(obj.boolean).isEqualTo(content.boolean)
        assertThat(obj.string).isEqualTo(content.string)
        assertThat(obj.stringUuid).isEqualTo(content.stringUuid)
        assertThat(obj.stringUri).isEqualTo(content.stringUri)
        assertThat(obj.stringDate).isEqualTo(content.stringDate)
        assertThat(obj.stringDateTime).isEqualTo(content.stringDateTime)
        assertThat(obj.number).isEqualTo(content.number)
        assertThat(obj.numberFloat).isEqualTo(content.numberFloat)
        assertThat(obj.numberDouble).isEqualTo(content.numberDouble)
        assertThat(obj.byte).isEqualTo(content.byte)
        assertThat(obj.binary).isEqualTo(content.binary)
    }

    @Test
    fun `fails to deserialize if custom serializer is not found`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        val e = assertThrows<SerializationException> {
            Json.decodeFromString<Content>(jsonString)
        }

        assertThat(e.message).containsPattern("Serializer for class '.*' is not found.")
    }
}