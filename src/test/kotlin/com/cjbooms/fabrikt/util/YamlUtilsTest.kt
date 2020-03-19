package com.cjbooms.fabrikt.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class YamlUtilsTest {

    private val inputYaml =
        """
        | ValidPreferentialCountries:
        |   type: "string"
        |   x-extensible-enum:
        |     - "NO"
        """.trimMargin()

    @Test
    fun `reading yaml tree does not strip inverted commas from string enum values`() {
        val jsonNode: JsonNode = YamlUtils.objectMapper.readValue(inputYaml)

        val enum = jsonNode.findValue("x-extensible-enum")
        assertTrue(enum.isArray)
        assertThat(enum.first().toString()).isEqualTo("\"NO\"")
    }

    @Test
    fun `merging yaml trees does not strip inverted commas from string enum values`() {
        val result = YamlUtils.mergeYamlTrees(inputYaml, inputYaml)
        val jsonNode: JsonNode = YamlUtils.objectMapper.readValue(result)

        val enum = jsonNode.findValue("x-extensible-enum")
        assertTrue(enum.isArray)
        assertThat(enum.first().toString()).isEqualTo("\"NO\"")
    }
}
