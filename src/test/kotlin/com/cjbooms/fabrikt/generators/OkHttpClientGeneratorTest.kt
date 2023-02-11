package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.OkHttpEnhancedClientGenerator
import com.cjbooms.fabrikt.generators.client.OkHttpSimpleClientGenerator
import com.cjbooms.fabrikt.generators.model.JacksonMetadata
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SimpleFile
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OkHttpClientGeneratorTest {

    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "okHttpClient",
        "okHttpClientMultiMediaType",
        "okHttpClientPostWithoutRequestBody",
        "pathLevelParameters",
        "parameterNameClash",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            modelOptions = setOf(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS),
        )
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api simple client is generated from a full API definition`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

        val expectedModel = readTextResource("/examples/$testCaseName/models/Models.kt")
        val expectedClient = readTextResource("/examples/$testCaseName/client/ApiClient.kt")

        val models = JacksonModelGenerator(
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
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

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
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

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
        val sourceApi = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))

        val expectedHttpUtils = readTextResource("/examples/$testCaseName/client/HttpUtil.kt")

        val generatedHttpUtils = OkHttpSimpleClientGenerator(
            packages,
            sourceApi
        ).generateLibrary().filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "HttpUtil.kt" }

        assertThat(generatedHttpUtils.content).isEqualTo(expectedHttpUtils)
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
