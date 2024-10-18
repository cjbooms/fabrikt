package com.cjbooms.fabrikt.models.kotlinx

import com.example.models.Pet
import kotlinx.serialization.encodeToString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinxSerializationSimpleTest {

    @Test
    fun `must serialize Pet`() {
        val pet = Pet(id = 1, name = "Fido", tag = "dog")
        val json = kotlinx.serialization.json.Json.encodeToString(pet)
        assertThat(json).isEqualTo("""
            {"id":1,"name":"Fido","tag":"dog"}
        """.trimIndent())
    }

    @Test
    fun `must deserialize Pet`() {
        val json = """
            {"id": 1, "name": "Fido", "tag": "dog"}
        """.trimIndent()
        val pet: Pet = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(pet).isEqualTo(Pet(id = 1, name = "Fido", tag = "dog"))
    }

    @Test
    fun `must serialize Pet with no tag`() {
        val pet = Pet(id = 1, name = "Whiskers")
        val json = kotlinx.serialization.json.Json.encodeToString(pet)
        assertThat(json).isEqualTo("""
            {"id":1,"name":"Whiskers"}
        """.trimIndent())
    }

    @Test
    fun `must deserialize Pet with no tag`() {
        val json = """
            {"id": 1, "name": "Whiskers"}
        """.trimIndent()
        val pet: Pet = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(pet).isEqualTo(Pet(id = 1, name = "Whiskers"))
    }
}
