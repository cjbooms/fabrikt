package com.cjbooms.fabrikt.models.jackson

import com.example.models.EnumHolder
import com.example.models.EnumHolderInlinedEnum
import com.example.models.EnumObject
import com.fasterxml.jackson.module.kotlin.jsonMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EnumTest {
    private val objectMapper = jsonMapper()
    private val writer = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    fun `must serialize array of enums`() {
        val wrapper = EnumHolder(arrayOfEnums = listOf("a", "b", "c"))
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/array_of_enums.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize array of enums`() {
        val result = readEnumHolder("array_of_enums")

        val expected = EnumHolder(arrayOfEnums = listOf("a", "b", "c"))
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize inlined enum`() {
        val wrapper = EnumHolder(inlinedEnum = EnumHolderInlinedEnum.INLINED_ONE)
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/inlined_enum.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize inlined enums`() {
        val result = readEnumHolder("inlined_enum")

        val expected = EnumHolder(inlinedEnum = EnumHolderInlinedEnum.INLINED_ONE)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize extensible enum`() {
        val wrapper = EnumHolder(inlinedExtensibleEnum = "new enum value")
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/extensible_enum.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize extensible enum`() {
        val result = readEnumHolder("extensible_enum")

        val expected = EnumHolder(inlinedExtensibleEnum = "new enum value")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize enum ref`() {
        val wrapper = EnumHolder(enumRef = EnumObject.ONE)
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/enum_ref.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize enum refs`() {
        val result = readEnumHolder("enum_ref")

        val expected = EnumHolder(enumRef = EnumObject.ONE)
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize extensible enum ref`() {
        val wrapper = EnumHolder(extensibleEnumRef = "extensible_enum_ref")
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/extensible_enum_ref.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize extensible enum refs`() {
        val result = readEnumHolder("extensible_enum_ref")

        val expected = EnumHolder(extensibleEnumRef = "extensible_enum_ref")
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must serialize a list of enum`() {
        val wrapper = EnumHolder(listEnums = listOf("list enum value 1"))
        val result = writer.writeValueAsString(wrapper)

        val expected = javaClass.getResource("/enums/list_of_enums.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `must deserialize a list of enum`() {
        val result = readEnumHolder("list_of_enums")

        val expected = EnumHolder(listEnums = listOf("list enum value 1"))
        assertThat(result).isEqualTo(expected)
    }

    private fun readEnumHolder(name: String): EnumHolder {
        val jsonString = javaClass.getResource("/enums/$name.json")!!.readText()

        return objectMapper.readValue(jsonString, EnumHolder::class.java)
    }
}