package com.cjbooms.fabrikt.models.jackson

import com.example.stringformat.models.Content
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringFormatTest {
    private val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(KotlinModule.Builder().build())
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

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
        val result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(content)

        val expected = javaClass.getResource("/primitive_types/content_valid_strings.json")!!.readText()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize Content`() {
        val jsonString = javaClass.getResource("/primitive_types/content_valid_strings.json")!!.readText()

        val obj = objectMapper.readValue<Content>(jsonString)

        assertThat(obj).isEqualTo(content)
    }
}
