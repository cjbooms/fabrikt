package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ExternalReferencesResolutionMode
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.OkHttpEnhancedClientGenerator
import com.cjbooms.fabrikt.generators.client.OkHttpSimpleClientGenerator
import com.cjbooms.fabrikt.generators.model.JacksonMetadata
import com.cjbooms.fabrikt.generators.model.ModelGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SimpleFile
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ModelNameRegistry
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Paths
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import org.junit.jupiter.api.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OkHttpClientGeneratorTest {

    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "okHttpClient",
        "multiMediaType",
        "okHttpClientPostWithoutRequestBody",
        "pathLevelParameters",
        "parameterNameClash",
        "byteArrayStream",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            clientTarget = ClientCodeGenTargetType.OK_HTTP,
            modelOptions = setOf(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS),
            typeOverrides = setOf(CodeGenTypeOverride.BYTEARRAY_AS_INPUTSTREAM)
        )
        ModelNameRegistry.clear()
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api simple client is generated from a full API definition`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedModel = readTextResource("/examples/$testCaseName/models/ClientModels.kt")
        val expectedClient = readTextResource("/examples/$testCaseName/client/ApiClient.kt")

        val models = ModelGenerator(
            packages,
            sourceApi
        ).generate().toSingleFile()
        val simpleClientCode = OkHttpSimpleClientGenerator(
            packages,
            sourceApi
        )
            .generateDynamicClientCode()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModel)
        assertThat(simpleClientCode).isEqualTo(expectedClient)
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api fault-tolerant service client is generated when the resilience4j option is set`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedLibUtil = readTextResource("/examples/$testCaseName/client/HttpResilience4jUtil.kt")
        val expectedClientCode = readTextResource("/examples/$testCaseName/client/ApiService.kt")

        val generator =
            OkHttpEnhancedClientGenerator(packages, sourceApi)
        val enhancedLibUtil = generator.generateLibrary(setOf(ClientCodeGenOptionType.RESILIENCE4J))
            .filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "HttpResilience4jUtil.kt" }
        val enhancedClientCode = generator.generateDynamicClientCode(setOf(ClientCodeGenOptionType.RESILIENCE4J))

        assertThat(enhancedLibUtil.content).isEqualTo(expectedLibUtil)
        assertThat(enhancedClientCode.toSingleFile()).isEqualTo(expectedClientCode)
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `the enhanced client is not generated when no specific options are provided`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val enhancedClientCode = OkHttpEnhancedClientGenerator(
            packages,
            sourceApi
        )
            .generateDynamicClientCode(emptySet())

        assertThat(enhancedClientCode).isEqualTo(emptySet<ClientType>())
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct http utility libraries are generated`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedHttpUtils = readTextResource("/examples/$testCaseName/client/HttpUtil.kt")

        val generatedHttpUtils = OkHttpSimpleClientGenerator(
            packages,
            sourceApi
        ).generateLibrary().filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "HttpUtil.kt" }

        assertThat(generatedHttpUtils.content).isEqualTo(expectedHttpUtils)
    }

    @Test
    fun `correct api client and models are generated with external reference solution mode AGGRESSIVE`() {
        val packages = Packages("examples.externalReferences.aggressive")
        val apiLocation = javaClass.getResource("/examples/externalReferences/aggressive/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedModel = readTextResource("/examples/externalReferences/aggressive/models/ClientModels.kt")
        val expectedClient = readTextResource("/examples/externalReferences/aggressive/client/ApiClient.kt")
        val expectedClientCode = readTextResource("/examples/externalReferences/aggressive/client/ApiService.kt")
        MutableSettings.updateSettings(
            externalRefResolutionMode = ExternalReferencesResolutionMode.AGGRESSIVE,
        )

        val models = ModelGenerator(
            packages,
            sourceApi,
        ).generate().toSingleFile()
        val generator =
            OkHttpEnhancedClientGenerator(packages, sourceApi)
        val simpleClientCode = OkHttpSimpleClientGenerator(
            packages,
            sourceApi
        )
            .generateDynamicClientCode()
            .toSingleFile()
        val enhancedClientCode = generator.generateDynamicClientCode(setOf(ClientCodeGenOptionType.RESILIENCE4J))
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModel)
        assertThat(simpleClientCode).isEqualTo(expectedClient)
        assertThat(enhancedClientCode).isEqualTo(expectedClientCode)
    }

    private fun Collection<ClientType>.toSingleFile(): String {
        val destPackage = if (this.isNotEmpty()) first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        this.forEach {
            val builder = singleFileBuilder
                .addType(it.spec)
                .addImport(JacksonMetadata.TYPE_REFERENCE_IMPORT.first, JacksonMetadata.TYPE_REFERENCE_IMPORT.second)
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
