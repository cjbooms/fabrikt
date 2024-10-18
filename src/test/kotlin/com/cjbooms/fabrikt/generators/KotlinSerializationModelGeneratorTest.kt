package com.cjbooms.fabrikt.generators

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ModelNameRegistry
import com.cjbooms.fabrikt.util.ResourceHelper.readFolder
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KotlinSerializationModelGeneratorTest {

    @Suppress("unused")
    private fun testCases(): Stream<String> = Stream.of(
//        "arrays",
//        "anyOfOneOfAllOf",
//        "deepNestedSharingReferences",
//        "defaultValues",
//        "duplicatePropertyHandling",
//        "enumExamples",
//        "enumPolymorphicDiscriminator",
//        "externalReferences/targeted",
//        "githubApi",
//        "inLinedObject",
//        "customExtensions",
//        "mapExamples",
//        "mapExamplesNonNullValues",
//        "mixingCamelSnakeLispCase",
//        "oneOfPolymorphicModels",
//        "optionalVsRequired",
//        "polymorphicModels",
//        "nestedPolymorphicModels",
//        "requiredReadOnly",
//        "validationAnnotations",
//        "wildCardTypes",
//        "singleAllOf",
//        "inlinedAggregatedObjects",
//        "responsesSchema",
//        "webhook",
//        "instantDateTime",
        "discriminatedOneOf",
//        "openapi310",
//        "binary",
//        "oneOfMarkerInterface",
//        "byteArrayStream",
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
            serializationLibrary = SerializationLibrary.KOTLINX_SERIALIZATION
        )
        ModelNameRegistry.clear()
    }

    // @Test
    // fun `debug single test`() = `correct models are generated for different OpenApi Specifications`("insert test case")

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        print("Testcase: $testCaseName")
        MutableSettings.addOption(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS)
        if (testCaseName == "instantDateTime") {
            MutableSettings.addOption(CodeGenTypeOverride.DATETIME_AS_INSTANT)
        }
        if (testCaseName == "discriminatedOneOf" || testCaseName == "oneOfMarkerInterface") {
            MutableSettings.addOption(ModelCodeGenOptionType.SEALED_INTERFACES_FOR_ONE_OF)
        }
        if (testCaseName == "mapExamplesNonNullValues") {
            MutableSettings.addOption(ModelCodeGenOptionType.NON_NULL_MAP_VALUES)
        }
        if (testCaseName == "byteArrayStream") {
            MutableSettings.addOption(CodeGenTypeOverride.BYTEARRAY_AS_INPUTSTREAM)
        }
        val basePackage = "examples.${testCaseName.replace("/", ".")}"
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))
        val expectedModels = readFolder(Path.of("src/test/resources/examples/$testCaseName/models/kotlinx/"))

        val models = JacksonModelGenerator(
            Packages(basePackage),
            sourceApi,
        ).generate()

        val sourceSet = setOf(KotlinSourceSet(models.files, Paths.get("")))
        val tempDirectory = Files.createTempDirectory("model_generator_test_${testCaseName.replace("/", ".")}")
        sourceSet.forEach {
            it.writeFileTo(tempDirectory.toFile())
        }

        val tempFolderContents =
            readFolder(tempDirectory.resolve(basePackage.replace(".", File.separator)).resolve("models"))
        tempFolderContents.forEach {
            if (expectedModels.containsKey(it.key)) {
                assertThat((it.value)).isEqualTo(expectedModels[it.key])
            } else {
                assertThat(it.value).isEqualTo("File not found in expected models")
            }
        }

        tempDirectory.toFile().deleteRecursively()
    }
}
