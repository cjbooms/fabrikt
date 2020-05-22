package com.cjbooms.fabrikt.client

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.OkHttpEnhancedClientGenerator
import com.cjbooms.fabrikt.generators.OkHttpSimpleClientGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SimpleFile
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.validation.Linter
import com.squareup.kotlinpoet.FileSpec
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OkHttpClientGeneratorTest {

    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "okHttpClient"
    )

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api simple client is generated from a full API definition`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())

        val expectedModel = javaClass.getResource("/examples/$testCaseName/models/Models.kt").readText()
        val expectedClient = javaClass.getResource("/examples/$testCaseName/client/ApiClient.kt").readText()

        val models = JacksonModelGenerator(packages.base, sourceApi).generate().toSingleFile()
        val simpleClientCode = OkHttpSimpleClientGenerator(packages, sourceApi)
            .generateDynamicClientCode()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModel)
        assertThat(simpleClientCode).isEqualTo(expectedClient)
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api fault-tolerant service client is generated when the resilience4j option is set`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())

        val expectedLibUtil = javaClass.getResource("/examples/$testCaseName/client/HttpResilience4jUtil.kt").readText()
        val expectedClientCode = javaClass.getResource("/examples/$testCaseName/client/ApiService.kt").readText()

        val generator = OkHttpEnhancedClientGenerator(packages, sourceApi)
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
        val sourceApi = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())

        val enhancedClientCode = OkHttpEnhancedClientGenerator(packages, sourceApi)
            .generateDynamicClientCode(emptySet())

        assertThat(enhancedClientCode).isEqualTo(emptySet<ClientType>())
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct http utitlity libraries are generated`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())

        val expectedHttpUtils = javaClass.getResource("/examples/$testCaseName/client/HttpUtil.kt").readText()

        val generatedHttpUtils = OkHttpSimpleClientGenerator(packages, sourceApi).generateLibrary().filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "HttpUtil.kt" }

        assertThat(generatedHttpUtils.content).isEqualTo(expectedHttpUtils)
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct logging interceptor libraries are generated`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val sourceApi = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())

        val expectedLoggingInterceptor = javaClass.getResource("/examples/$testCaseName/client/LoggingInterceptor.kt").readText()

        val generatedLoggingInterceptor = OkHttpSimpleClientGenerator(packages, sourceApi).generateLibrary().filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "LoggingInterceptor.kt" }

        assertThat(generatedLoggingInterceptor.content).isEqualTo(expectedLoggingInterceptor)
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
        models.forEach {
            val builder = singleFileBuilder
                .addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }
}
