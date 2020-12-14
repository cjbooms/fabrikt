package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.model.QuarkusReflectionModelGenerator
import com.cjbooms.fabrikt.model.ResourceFile
import com.cjbooms.fabrikt.model.SourceApi
import java.nio.file.Paths
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceGeneratorTest {

    private fun testCases(): Stream<String> = Stream.of(
        "githubApi"
    )

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct quarkus reflection resource is generated for different OpenApi Specifications`(testCaseName: String) {
        print("Testcase: $testCaseName")
        val basePackage = "examples.$testCaseName"
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))
        val expectedResource = javaClass.getResource("/examples/$testCaseName/resources/reflection-config.json").readText()
        val options = setOf(ModelCodeGenOptionType.QUARKUS_REFLECTION_CONFIG)

        val models = JacksonModelGenerator(Packages(basePackage), sourceApi, options).generate()

        val resources = QuarkusReflectionModelGenerator(models, options).generate()?.toSingleFile()

        assertThat(resources).isEqualTo(expectedResource)
    }

    @ParameterizedTest
    @MethodSource("testCases")
    fun `quarkus reflection resource is not generated if the option is not provided`(testCaseName: String) {
        print("Testcase: $testCaseName")
        val basePackage = "examples.$testCaseName"
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val models = JacksonModelGenerator(Packages(basePackage), sourceApi).generate()

        val resources = QuarkusReflectionModelGenerator(models).generate()?.toSingleFile()

        assertThat(resources).isNull()
    }

    private fun ResourceFile.toSingleFile(): String = String(inputStream.readAllBytes())
}
