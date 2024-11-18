package com.cjbooms.fabrikt.models.kotlinx

import com.example.models.LandlinePhone
import com.example.models.Phone
import kotlinx.serialization.encodeToString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinxSerializationOneOfPolymorphicTest {

    @Test
    fun `must serialize Phone with type info`() {
        val phone: Phone = LandlinePhone(number = "1234567890", areaCode = "123")
        val json = kotlinx.serialization.json.Json.encodeToString(phone)

        // Note that "type" is added because we are serializing a subtype of Phone
        // (See https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#sealed-classes)
        assertThat(json).isEqualTo("""
            {"type":"landline","number":"1234567890","area_code":"123"}
        """.trimIndent())
    }

    @Test
    fun `must serialize LandlinePhone without type info`() {
        val phone: LandlinePhone = LandlinePhone(number = "1234567890", areaCode = "123")
        val json = kotlinx.serialization.json.Json.encodeToString(phone)

        // Note that "type" is not added because we are serializing the specific class LandlinePhone
        // (See https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/polymorphism.md#sealed-classes)
        assertThat(json).isEqualTo("""
            {"number":"1234567890","area_code":"123"}
        """.trimIndent())
    }

    @Test
    fun `must deserialize Phone into LandlinePhone`() {
        val json = """
            {"type":"landline","number":"1234567890","area_code":"123"}
        """.trimIndent()
        val phone: Phone = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(phone).isEqualTo(LandlinePhone(number = "1234567890", areaCode = "123"))
    }

    @Test
    fun `must deserialize LandlinePhone specific class`() {
        val json = """
            {"number":"1234567890","area_code":"123"}
        """.trimIndent()
        val phone: LandlinePhone = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(phone).isEqualTo(LandlinePhone(number = "1234567890", areaCode = "123"))
    }
}
