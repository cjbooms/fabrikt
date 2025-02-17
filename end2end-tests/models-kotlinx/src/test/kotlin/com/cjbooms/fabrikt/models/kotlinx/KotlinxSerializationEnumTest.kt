package com.cjbooms.fabrikt.models.kotlinx

import com.example.models.TransportationDevice
import com.example.models.TransportationDeviceDeviceType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class KotlinxSerializationEnumTest {

    @Test
    fun `must serialize entity with enum field`() {
        val device = TransportationDevice(
            deviceType = TransportationDeviceDeviceType.BIKE,
            make = "Specialized",
            model = "Chisel"
        )
        val json = Json.encodeToString(device)
        assertThat(json).isEqualTo("""
            {"deviceType":"bike","make":"Specialized","model":"Chisel"}
        """.trimIndent())
    }

    @Test
    fun `must deserialize entity with enum field`() {
        val json = """
            {"deviceType":"bike","make":"Specialized","model":"Chisel"}
        """.trimIndent()
        val device = Json.decodeFromString(TransportationDevice.serializer(), json)
        assertThat(device).isEqualTo(
            TransportationDevice(
                deviceType = TransportationDeviceDeviceType.BIKE,
                make = "Specialized",
                model = "Chisel"
            )
        )
    }

    @Test
    fun `must fail with SerializationException if enum value is not valid`() {
        val json = """
            {"deviceType":"car","make":"Specialized","model":"Chisel"}
        """.trimIndent()
        val exception = assertThrows<SerializationException> {
            Json.decodeFromString<TransportationDevice>(json)
        }
        assertThat(exception.message).isEqualTo("com.example.models.TransportationDeviceDeviceType does not contain element with name 'car' at path \$.deviceType")
    }

    @Test
    fun `must fail with SerializationException if required fields are missing`() {
        val json = """
        {"deviceType":"bike"}
    """.trimIndent()
        val exception = assertThrows<SerializationException> {
            Json.decodeFromString<TransportationDevice>(json)
        }
        assertThat(exception.message).contains("Fields [make, model] are required for type with serial name 'com.example.models.TransportationDevice', but they were missing at path: \$")
    }

    @Test
    fun `must serialize entity with enum field with mixed case`() {
        val device = TransportationDevice(
            deviceType = TransportationDeviceDeviceType.HO_VER_BOA_RD,
            make = "Hover",
            model = "Board"
        )
        val json = Json.encodeToString(device)
        assertThat(json).isEqualTo("""
            {"deviceType":"Ho_ver-boaRD","make":"Hover","model":"Board"}
        """.trimIndent())
    }

    @Test
    fun `must deserialize entity with enum field with mixed case`() {
        val json = """
            {"deviceType":"Ho_ver-boaRD","make":"Hover","model":"Board"}
        """.trimIndent()
        val device = Json.decodeFromString<TransportationDevice>(json)
        assertThat(device).isEqualTo(
            TransportationDevice(
                deviceType = TransportationDeviceDeviceType.HO_VER_BOA_RD,
                make = "Hover",
                model = "Board"
            )
        )
    }
}
