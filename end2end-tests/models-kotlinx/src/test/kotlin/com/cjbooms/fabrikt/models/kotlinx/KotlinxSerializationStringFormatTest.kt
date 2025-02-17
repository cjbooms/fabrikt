package com.cjbooms.fabrikt.models.kotlinx

import com.example.stringformat.models.Content
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * This test verifies that serialization and deserialization of a data class with various primitive types
 * works as expected when the type overrides for string formats are in effect:
 *
 * - UUID_AS_STRING
 * - URI_AS_STRING
 * - BYTE_AS_STRING
 * - BINARY_AS_STRING
 * - DATE_AS_STRING
 * - DATETIME_AS_STRING
 */
class KotlinxSerializationStringFormatTest {

    private val json = Json {
        prettyPrint = true
    }

    private val content = Content(
        string = "example",
        stringUuid = "123e4567-e89b-12d3-a456-426614174000",
        stringUri = "https://example.org",
        stringDate = "2020-02-04",
        stringDateTime = "2024-11-04T12:00:00Z",
        byte = "AETdqhOI3C8/jA184vF3FyUNGesJ9x22cn2TqiQLYpFzvy5Moyie3K1MAy8DVy62HxURtRHwP2SjdV7B+HZQzuCwMsJLxhbNj0okOzdV2EOAr2JV3htYH+vNVJE9NHwzyYTkOA5ZuYpEDZMEL+SqjyeSRXaLimqDbkew6hg1QdU=",
        binary = "AETdqhOI3C8/jA184vF3FyUNGesJ9x22cn2TqiQLYpFzvy5Moyie3K1MAy8DVy62HxURtRHwP2SjdV7B+HZQzuCwMsJLxhbNj0okOzdV2EOAr2JV3htYH+vNVJE9NHwzyYTkOA5ZuYpEDZMEL+SqjyeSRXaLimqDbkew6hg1QdU="
    )

    @Test
    fun `must serialize Content`() {
        val result = json.encodeToString(content)

        val expected = javaClass.getResource("/primitive_types/content_valid_strings.json")!!.readText()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize Content`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid_strings.json")!!.readText()

        val obj: Content = json.decodeFromString(jsonString)

        assertThat(obj).isEqualTo(content)
    }
}