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

    @Test
    fun `encoding object is correctly parsed by Kaizen parser`() {
        val encodingInput =
            """
        | openapi: 3.0.1
        | info:
        |   title: Sample API
        |   version: "1.0"
        | paths:
        |   /endpoint:
        |     post:
        |       requestBody:
        |         required: true
        |         content:
        |           application/json:
        |             schema:
        |               type: object
        |               properties:
        |                 image:
        |                   type: string
        |                   format: binary
        |             encoding:
        |               image:
        |                 contentType: image/png, image/jpeg, image/webp
        |       responses:
        |         200:
        |           description: Everything OK
        """.trimMargin()

        val api = YamlUtils.parseOpenApi(encodingInput)

        assertThat(api.isValid).isEqualTo(true)
    }
}
