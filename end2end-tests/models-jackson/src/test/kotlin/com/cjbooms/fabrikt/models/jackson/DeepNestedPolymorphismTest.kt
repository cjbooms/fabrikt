package com.cjbooms.fabrikt.models.jackson

import com.cjbooms.fabrikt.models.jackson.Helpers.mapper
import com.example.models.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeepNestedPolymorphismTest {
    private val objectMapper = mapper()
    private val writer = objectMapper.writerWithDefaultPrettyPrinter()

    @Test
    fun `Must serialize deep nested-polymorphism ThirdLevelChild11`() {
        val obj = ThirdLevelChild11(rootField1 = "root field 1", rootField2 = false, creationDate = 1,
            firstLevelField1 = "first level field 1", metadata = SecondLevelMetadata(obj = CommonObject(filed1 = "field 1", field2 = "field 2")))

        val result = writer.writeValueAsString(obj)

        val expected = javaClass.getResource("/deep_nested_polymorphic/third_level_child11.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `Must deserialize deep nested-polymorphism ThirdLevelChild11`() {
        val jsonString = javaClass.getResource("/deep_nested_polymorphic/third_level_child11.json")!!.readText()

        val result = objectMapper.readValue(jsonString, SecondLevelChild1::class.java)

        val expected = ThirdLevelChild11(rootField1 = "root field 1", rootField2 = false, creationDate = 1,
            firstLevelField1 = "first level field 1", metadata = SecondLevelMetadata(obj = CommonObject(filed1 = "field 1", field2 = "field 2")))

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `Must serialize deep nested-polymorphism ThirdLevelChild12`() {
        val obj = ThirdLevelChild12(rootField1 = "root field 1", rootField2 = false, isDeleted = false,
            firstLevelField1 = "first level field 1", metadata = SecondLevelMetadata(obj = CommonObject(filed1 = "field 1", field2 = "field 2")))

        val result = writer.writeValueAsString(obj)

        val expected = javaClass.getResource("/deep_nested_polymorphic/third_level_child12.json")!!.readText()
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `Must deserialize deep nested-polymorphism ThirdLevelChild12`() {
        val jsonString = javaClass.getResource("/deep_nested_polymorphic/third_level_child12.json")!!.readText()

        val result = objectMapper.readValue(jsonString, SecondLevelChild1::class.java)

        val expected = ThirdLevelChild12(rootField1 = "root field 1", rootField2 = false, isDeleted = false,
            firstLevelField1 = "first level field 1", metadata = SecondLevelMetadata(obj = CommonObject(filed1 = "field 1", field2 = "field 2")))

        assertThat(result).isEqualTo(expected)
    }
}