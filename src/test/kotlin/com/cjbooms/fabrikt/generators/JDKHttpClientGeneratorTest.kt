package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.JDKHttpSimpleClientGenerator
import com.cjbooms.fabrikt.generators.client.OkHttpSimpleClientGenerator
import com.cjbooms.fabrikt.generators.model.JacksonMetadata
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SimpleFile
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ModelNameRegistry
import com.cjbooms.fabrikt.util.ResourceHelper
import com.squareup.kotlinpoet.FileSpec
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JDKHttpClientGeneratorTest {

    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "httpClient",
        "multiMediaType",
        /*"okHttpClientPostWithoutRequestBody",
        "pathLevelParameters",
        "parameterNameClash"*/
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            clientTarget = ClientCodeGenTargetType.JDK_HTTP,
            modelOptions = setOf(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS),
        )
        ModelNameRegistry.clear()
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct api simple client is generated from a full API definition`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName.jdk")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedModel =
            ResourceHelper.readTextResource("/examples/$testCaseName/models/jdk/Models.kt")
        val expectedClient =
            ResourceHelper.readTextResource("/examples/$testCaseName/client/jdk/ApiClient.kt")

        val models = JacksonModelGenerator(
            packages,
            sourceApi
        ).generate().toSingleFile()
        val simpleClientCode = JDKHttpSimpleClientGenerator(
            packages,
            sourceApi
        )
            .generateDynamicClientCode()
            .toSingleFile()

        Assertions.assertThat(models).isEqualTo(expectedModel)
        Assertions.assertThat(simpleClientCode).isEqualTo(expectedClient)
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct http utility libraries are generated`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName.jdk")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedHttpUtils =
            ResourceHelper.readTextResource("/examples/$testCaseName/client/jdk/HttpUtil.kt")

        val generatedHttpUtils = JDKHttpSimpleClientGenerator(
            packages,
            sourceApi
        ).generateLibrary().filterIsInstance<SimpleFile>()
            .first { it.path.fileName.toString() == "HttpUtil.kt" }

        Assertions.assertThat(generatedHttpUtils.content).isEqualTo(expectedHttpUtils)
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