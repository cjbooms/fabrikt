package com.cjbooms.fabrikt.model

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.generators.MutableSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SourceApiTest {

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(setOf(CodeGenerationType.HTTP_MODELS), emptySet(), emptySet())
    }

    @Test
    fun testParentToChildren() {
        val spec = """
            openapi: 3.0.0
            components:
              schemas:

                Person:
                  type: object
                  properties:
                    name:
                      type: string

                Adult:
                  description: A representation of an adult
                  allOf:
                  - ${'$'}ref: '#/components/schemas/Person'
                  - type: object
                    properties:
                      address:
                        type: string

                Child:
                  description: A representation of a child
                  allOf:
                  - ${'$'}ref: '#/components/schemas/Person'
                  - type: object
                    properties:
                      age:
                        type: integer
        """.trimIndent()

        val SourceApi = SourceApi(spec)

        assertThat(SourceApi.modelInfos).anyMatch {
            it.name == "Person" && it.childSchemas.containsAll(listOf("Adult", "Child"))
        }
    }

    @Test
    fun `api fragments are successfully merged`() {
        val spec = """
            openapi: 3.0.0
            info:
            components:
              schemas:
                Person:
                  type: object
                  properties:
                    name:
                      type: string
        """.trimIndent()
        val fragment = """
            openapi: 3.0.0
            info:
              x-fabric-event-type: "some-nakadi-event-type"
            components:
              schemas:
                EventData:
                  type: object
                  properties:
                    name:
                      type: string
        """.trimIndent()
        val expected = """
            openapi: "3.0.0"
            info:
              x-fabric-event-type: "some-nakadi-event-type"
            components:
              schemas:
                Person:
                  type: "object"
                  properties:
                    name:
                      type: "string"
                EventData:
                  type: "object"
                  properties:
                    name:
                      type: "string"
        """.trimIndent()

        val SourceApi = SourceApi.create(spec, setOf(fragment))

        assertThat(SourceApi.rawApiSpec).isEqualToIgnoringNewLines(expected)
    }

    @Test
    fun testParentToChildrenWithMultipleParents() {
        val spec = """
            openapi: 3.0.0
            components:
              schemas:

                PersonA:
                  type: object
                  properties:
                    name:
                      type: string

                PersonB:
                  type: object
                  properties:
                    name:
                      type: string

                Adult:
                  description: A representation of an adult
                  allOf:
                  - ${'$'}ref: '#/components/schemas/PersonA'
                  - ${'$'}ref: '#/components/schemas/PersonB'
                  - type: object
                    properties:
                      address:
                        type: string

                Child:
                  description: A representation of a child
                  allOf:
                  - ${'$'}ref: '#/components/schemas/PersonA'
                  - type: object
                    properties:
                      age:
                        type: integer
        """.trimIndent()

        val SourceApi = SourceApi(spec)

        assertThat(SourceApi.modelInfos).anyMatch {
            it.name == "PersonA" && it.childSchemas.containsAll(listOf("Adult", "Child"))
        }
        assertThat(SourceApi.modelInfos).anyMatch {
            it.name == "PersonB" && it.childSchemas.containsAll(listOf("Adult"))
        }
    }

    @Test
    fun testParentToChildToAnotherChild() {
        val spec = """
            openapi: 3.0.0
            components:
              schemas:

                PersonA:
                  type: object
                  properties:
                    name:
                      type: string

                PersonB:
                  type: object
                  allOf:
                  - ${'$'}ref: '#/components/schemas/PersonA'
                  - type: object
                    properties:
                      name:
                        type: string

                Adult:
                  description: A representation of an adult
                  allOf:
                  - ${'$'}ref: '#/components/schemas/PersonB'
                  - type: object
                    properties:
                      address:
                        type: string

                Child:
                  description: A representation of a child
                  allOf:
                  - ${'$'}ref: '#/components/schemas/PersonB'
                  - type: object
                    properties:
                      age:
                        type: integer
        """.trimIndent()

        val SourceApi = SourceApi(spec)

        assertThat(SourceApi.modelInfos).anyMatch {
            it.name == "PersonB" && it.childSchemas.containsAll(listOf("Adult", "Child"))
        }
        assertThat(SourceApi.modelInfos).anyMatch {
            it.name == "PersonA" && it.childSchemas.containsAll(listOf("PersonB"))
        }
    }

    @Test
    fun `Missing refs in the input throws an understandable error message`() {
        val spec1 = javaClass.getResource("/badInput/HandleMissingRefObject.yaml").readText()
        val spec2 = javaClass.getResource("/badInput/HandleMissingRefArray.yaml").readText()

        val exception1 = assertThrows<ParameterException> {
            SourceApi(spec1)
        }

        val exception2 = assertThrows<ParameterException> {
            SourceApi(spec2)
        }

        assertThat(exception1.message).contains("Property 'propB' cannot be parsed to a Schema. Check your input")
        assertThat(exception2.message).contains("Array type 'hooks' cannot be parsed to a Schema. Check your input")
    }
}
