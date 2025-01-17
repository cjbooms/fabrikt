package com.cjbooms.fabrikt.models.kotlinx

import com.example.polymorphicmodels.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class KotlinxSerializationPolymorphicModelsTest {

    @Nested
    inner class PolymorphicSuperTypeTests {

        @Test
        fun `must serialize PolymorphicTypeOne with type info`() {
            val polymorphicType: PolymorphicSuperType = PolymorphicTypeOne(firstName = "John", lastName = "Doe", childOneName = "ChildOne")
            val json = Json.encodeToString(polymorphicType)

            assertThat(json).isEqualTo("""
            {"generation":"com.example.polymorphicmodels.models.PolymorphicTypeOne","first_name":"John","last_name":"Doe","child_one_name":"ChildOne"}
        """.trimIndent())// TODO: Should be "generation":"PolymorphicTypeOne
        }

        @Test
        fun `must serialize PolymorphicTypeTwo with type info`() {
            val polymorphicType: PolymorphicSuperType = PolymorphicTypeTwo(firstName = "Jane", lastName = "Doe", someIntegerPropery = 42, childTwoAge = 10)
            val json = Json.encodeToString(polymorphicType)

            assertThat(json).isEqualTo("""
            {"generation":"PolymorphicTypeTwo","first_name":"Jane","last_name":"Doe","some_integer_propery":42,"child_two_age":10}
        """.trimIndent())
        }

        @Test
        fun `must deserialize PolymorphicTypeOne`() {
            val json = """
            {"generation":"PolymorphicTypeOne","first_name":"John","last_name":"Doe","child_one_name":"ChildOne"}
        """.trimIndent()
            val polymorphicType: PolymorphicSuperType = Json.decodeFromString(json)
            assertThat(polymorphicType).isEqualTo(PolymorphicTypeOne(firstName = "John", lastName = "Doe", childOneName = "ChildOne"))
        }

        @Test
        fun `must deserialize PolymorphicTypeTwo`() {
            val json = """
            {"generation":"PolymorphicTypeTwo","first_name":"Jane","last_name":"Doe","some_integer_propery":42,"child_two_age":10}
        """.trimIndent()
            val polymorphicType: PolymorphicSuperType = Json.decodeFromString(json)
            assertThat(polymorphicType).isEqualTo(PolymorphicTypeTwo(firstName = "Jane", lastName = "Doe", someIntegerPropery = 42, childTwoAge = 10))
        }
    }
}