package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.SpringHttpInterfaceGenerator
import com.cjbooms.fabrikt.generators.model.ModelGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ModelNameRegistry
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
class SpringHttpInterfaceGeneratorTest {
    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "springHttpInterfaceClient",
        "multiMediaType",
        "pathLevelParameters",
        "parameterNameClash",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            clientTarget = ClientCodeGenTargetType.SPRING_HTTP_INTERFACE,
            modelOptions = setOf(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS),
        )
        ModelNameRegistry.clear()
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct Spring HTTP Interface clients are generated from a full API definition`(testCaseName: String) {
        runTestCase(testCaseName)
    }

    @Test
    fun `correct Spring HTTP Interface clients are generated with suspend modifier`() {
        runTestCase(
            "springHttpInterfaceClient",
            clientFileName = "SuspendableSpringHttpInterfaceClient.kt",
            options = setOf(ClientCodeGenOptionType.SUSPEND_MODIFIER),
        )
    }

    @Test
    fun `correct Spring HTTP Interface clients are generated with response entity wrapper and spring annotation`() {
        runTestCase(
            "springHttpInterfaceClient",
            clientFileName = "SpringHttpInterfaceClientWithResponseEntity.kt",
            options = setOf(
                ClientCodeGenOptionType.SPRING_RESPONSE_ENTITY_WRAPPER,
            )
        )
    }

    private fun runTestCase(
        testCaseName: String,
        clientFileName: String = "SpringHttpInterfaceClient.kt",
        options: Set<ClientCodeGenOptionType> = emptySet(),
    ) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

        val expectedModel = readTextResource("/examples/$testCaseName/models/ClientModels.kt")
        val expectedClient = readTextResource("/examples/$testCaseName/client/$clientFileName")

        val models = ModelGenerator(
            packages,
            sourceApi,
        ).generate().toSingleFile()
        val clientCode = SpringHttpInterfaceGenerator(
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
