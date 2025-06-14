package com.cjbooms.fabrikt.models.jackson

import com.cjbooms.fabrikt.models.jackson.Helpers.mapper
import com.example.suffixed.models.PolymorphicSuperTypeModelSuffix
import com.example.suffixed.models.PolymorphicTypeOneModelSuffix
import com.example.suffixed.models.PolymorphicTypeTwoModelSuffix
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SuffixedModelPolymorphismTest {
    private val objectMapper = mapper()
    private val writer = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    fun `must serialize PolymorphicTypeOneModelSuffix`() {
        val obj = PolymorphicTypeOneModelSuffix(firstName = "first name", lastName = "last name", childOneName = "child on name")
        val result = writer.writeValueAsString(obj)

        val expected = javaClass.getResource("/polymorphic/polymorphic_type_one.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize PolymorphicTypeOneModelSuffix`() {
        val jsonString = javaClass.getResource("/polymorphic/polymorphic_type_one.json")!!.readText()

        val result = objectMapper.readValue(jsonString, PolymorphicSuperTypeModelSuffix::class.java)
        assertThat(result).isEqualTo(PolymorphicTypeOneModelSuffix(firstName = "first name", lastName = "last name", childOneName = "child on name"))
    }

    @Test
    fun `must serialize PolymorphicTypeTwoModelSuffix`() {
        val obj = PolymorphicTypeTwoModelSuffix(firstName = "first name", lastName = "last name", childTwoAge = 1)
        val result = writer.writeValueAsString(obj)

        val expected = javaClass.getResource("/polymorphic/polymorphic_type_two.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize PolymorphicTypeTwoModelSuffix`() {
        val jsonString = javaClass.getResource("/polymorphic/polymorphic_type_two.json")!!.readText()

        val result = objectMapper.readValue(jsonString, PolymorphicSuperTypeModelSuffix::class.java)
        assertThat(result).isEqualTo(PolymorphicTypeTwoModelSuffix(firstName = "first name", lastName = "last name", childTwoAge = 1))
    }
}