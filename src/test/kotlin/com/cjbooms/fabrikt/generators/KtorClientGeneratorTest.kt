package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.KtorClientGenerator
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.FileUtils.toSingleFile
import com.cjbooms.fabrikt.util.GeneratedCodeAsserter.Companion.assertThatGenerated
import com.cjbooms.fabrikt.util.ModelNameRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.nio.file.Paths
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KtorClientGeneratorTest {

    @Suppress("unused")
    private fun fullApiTestCases(): Stream<String> = Stream.of(
        "ktorClient",
        "parameterNameClash",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.CLIENT),
            clientTarget = ClientCodeGenTargetType.KTOR,
            serializationLibrary = SerializationLibrary.KOTLINX_SERIALIZATION,
            validationLibrary = ValidationLibrary.NO_VALIDATION,
        )
        ModelNameRegistry.clear()
    }

    @ParameterizedTest
    @MethodSource("fullApiTestCases")
    fun `correct Ktor routing resources are generated`(testCaseName: String) {
        val packages = Packages("examples.$testCaseName")
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))

        val expectedClient = "/examples/$testCaseName/client/ktor/KtorClient.kt"

        val clientCode = KtorClientGenerator(
            packages,
            sourceApi
        )
            .generate(emptySet())
            .clients
            .toSingleFile()

        assertThatGenerated(clientCode).isEqualTo(expectedClient)
    }
}