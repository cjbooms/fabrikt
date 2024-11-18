package com.cjbooms.fabrikt.models.kotlinx

import com.example.models.Pet
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.encodeToString
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class KotlinxSerializationSimpleTest {

    @Test
    fun `must serialize Pet`() {
        val pet = Pet(
            id = 1,
            name = "Fido",
            tag = "dog",
            dateOfBirth = LocalDate.parse("2020-02-04"),
            lastFedAt = Instant.parse("2024-11-04T12:00:00Z")
        )
        val json = kotlinx.serialization.json.Json.encodeToString(pet)
        assertThat(json).isEqualTo(
            """
            {"id":1,"name":"Fido","tag":"dog","dateOfBirth":"2020-02-04","lastFedAt":"2024-11-04T12:00:00Z"}
        """.trimIndent()
        )
    }

    @Test
    fun `must deserialize Pet`() {
        val json = """
            {"id": 1, "name": "Fido", "tag": "dog", "dateOfBirth": "2009-02-13", "lastFedAt": "2011-02-04T10:00:00Z", "earTagUuid": "123e4567-e89b-12d3-a456-426614174000", "imageUrl": "https://example.org/image.jpg"}
        """.trimIndent()
        val pet: Pet = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(pet).isEqualTo(
            Pet(
                id = 1,
                name = "Fido",
                tag = "dog",
                dateOfBirth = LocalDate.parse("2009-02-13"),
                lastFedAt = Instant.parse("2011-02-04T10:00:00Z"),
                earTagUuid = "123e4567-e89b-12d3-a456-426614174000", // string - no native UUIDin Kotlin (yet?)
                imageUrl = "https://example.org/image.jpg" // string - no native URL in Kotlin (yet?)
            )
        )
    }

    @Test
    fun `must serialize Pet with no tag`() {
        val pet = Pet(id = 1, name = "Whiskers", dateOfBirth = LocalDate.parse("2011-03-15"))
        val json = kotlinx.serialization.json.Json.encodeToString(pet)
        assertThat(json).isEqualTo(
            """
            {"id":1,"name":"Whiskers","dateOfBirth":"2011-03-15"}
        """.trimIndent()
        )
    }

    @Test
    fun `must deserialize Pet with no tag`() {
        val json = """
            {"id": 1, "name": "Whiskers", "dateOfBirth": "2024-09-24"}
        """.trimIndent()
        val pet: Pet = kotlinx.serialization.json.Json.decodeFromString(json)
        assertThat(pet).isEqualTo(Pet(id = 1, name = "Whiskers", dateOfBirth = LocalDate.parse("2024-09-24")))
    }
}
