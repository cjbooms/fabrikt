package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.OpenFeignInterfaceGenerator
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OpenFeignClientGeneratorTest {

    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "openFeignClient",
        "multiMediaType",
        "pathLevelParameters",
        "parameterNameClash",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            clientTarget = ClientCodeGenTargetType.OPEN_FEIGN,
            modelOptions = setOf(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS),
        )
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct Open Feign interfaces are generated from a full API definition`(testCaseName: String) {
        runTestCase(testCaseName)
    }

    @Test
    fun `correct Open Feign interfaces are generated with suspend modifier`() {
        runTestCase(
            testCaseName = "openFeignClient",
            clientFileName = "SuspendableOpenFeignClient.kt",
            options = setOf(ClientCodeGenOptionType.SUSPEND_MODIFIER),
        )
    }

    private fun runTestCase(
        testCaseName: String,
        clientFileName: String = "OpenFeignClient.kt",
        options: Set<ClientCodeGenOptionType> = emptySet(),
    ) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

        val expectedModel = readTextResource("/examples/$testCaseName/models/Models.kt")
        val expectedClient = readTextResource("/examples/$testCaseName/client/$clientFileName")

        val models = JacksonModelGenerator(
            packages,
            sourceApi,
        ).generate().toSingleFile()
        val clientCode = OpenFeignInterfaceGenerator(
            packages,
            sourceApi,
        )
            .generate(options)
            .clients
            .toSingleFile()

        assertThat(clientCode).isEqualTo(expectedClient)
        assertThat(models).isEqualTo(expectedModel)
    }

    private fun Collection<ClientType>.toSingleFile(): String {
        val destPackage = if (this.isNotEmpty()) first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        this.forEach {
            val builder = singleFileBuilder
                .addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }

    private fun Models.toSingleFile(): String {
        val destPackage = if (models.isNotEmpty()) models.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        models
            .sortedBy { it.spec.name }
            .forEach {
                val builder = singleFileBuilder
                    .addType(it.spec)
                builder.build()
            }
        return Linter.lintString(singleFileBuilder.build().toString())
    }
}
