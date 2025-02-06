package com.cjbooms.fabrikt.models.jackson

import com.example.primitives.models.Content
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

class PrimitiveTypesTest {
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    @Test
    fun `must serialize Content`() {
        val content = Content(
            integer = 1,
            integer32 = 2147483647,
            integer64 = 9223372036854775807,
            boolean = true,
            string = "example",
            stringUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            stringUri = URI("https://example.org"),
            stringDate = LocalDate.parse("2020-02-04"),
            stringDateTime = OffsetDateTime.parse("2024-11-04T12:00:00Z"),
            number = BigDecimal("109288282772724.4225837838838383888"),
            numberFloat = 1.23f,
            numberDouble = 4.56,
            byte = javaClass.getResource("/primitive_types/test.bin")!!.readBytes(),
            binary = javaClass.getResource("/primitive_types/test.bin")!!.readBytes()
        )

        val result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(content)

        val expected = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize Content`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid.json")!!.readText()
        val binFileContent = javaClass.getResource("/primitive_types/test.bin")!!.readBytes()

        val obj = objectMapper.readValue<Content>(jsonString)

        val expectedContent = Content(
            integer = 1,
            integer32 = 2147483647,
            integer64 = 9223372036854775807,
            boolean = true,
            string = "example",
            stringUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            stringUri = URI("https://example.org"),
            stringDate = LocalDate.parse("2020-02-04"),
            stringDateTime = OffsetDateTime.parse("2024-11-04T12:00:00Z"),
            number = BigDecimal("109288282772724.4225837838838383888"),
            numberFloat = 1.23f,
            numberDouble = 4.56,
            byte = binFileContent,
            binary = binFileContent
        )

        assertThat(obj.integer).isEqualTo(expectedContent.integer)
        assertThat(obj.integer32).isEqualTo(expectedContent.integer32)
        assertThat(obj.integer64).isEqualTo(expectedContent.integer64)
        assertThat(obj.boolean).isEqualTo(expectedContent.boolean)
        assertThat(obj.string).isEqualTo(expectedContent.string)
        assertThat(obj.stringUuid).isEqualTo(expectedContent.stringUuid)
        assertThat(obj.stringUri).isEqualTo(expectedContent.stringUri)
        assertThat(obj.stringDate).isEqualTo(expectedContent.stringDate)
        assertThat(obj.stringDateTime).isEqualTo(expectedContent.stringDateTime)
        assertThat(obj.number).isEqualTo(expectedContent.number)
        assertThat(obj.numberFloat).isEqualTo(expectedContent.numberFloat)
        assertThat(obj.numberDouble).isEqualTo(expectedContent.numberDouble)
        assertThat(obj.byte).isEqualTo(expectedContent.byte)
        assertThat(obj.binary).isEqualTo(expectedContent.binary)
    }
}