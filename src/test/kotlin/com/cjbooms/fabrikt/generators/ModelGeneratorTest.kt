package com.cjbooms.fabrikt.generators

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.ValidationLibrary
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.model.ModelGenerator
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
class ModelGeneratorTest {

    @Suppress("unused")
    private fun testCases(): Stream<String> = Stream.of(
        "additionalProperties",
        "arrays",
        "anyOfOneOfAllOf",
        "deepNestedSharingReferences",
        "defaultValues",
        "duplicatePropertyHandling",
        "enumExamples",
        "enumPolymorphicDiscriminator",
        "externalReferences/targeted",
        "githubApi",
        "inLinedObject",
        "customExtensions",
        "mapExamples",
        "mapExamplesNonNullValues",
        "mixingCamelSnakeLispCase",
        "oneOfPolymorphicModels",
        "optionalVsRequired",
        "polymorphicModels",
        "nestedPolymorphicModels",
        "requiredReadOnly",
        "validationAnnotations",
        "wildCardTypes",
        "singleAllOf",
        "inlinedAggregatedObjects",
        "responsesSchema",
        "webhook",
        "instantDateTime",
        "discriminatedOneOf",
        "openapi310",
        "openapi310OptionalObject",
        "binary",
        "oneOfMarkerInterface",
        "byteArrayStream",
        "untypedObject",
        "primitiveTypes"
    )

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
        )
        ModelNameRegistry.clear()
    }

    @Test
    fun `debug single test`() = `correct models are generated for different OpenApi Specifications`("enumExamples")

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        print("Testcase: $testCaseName")
        MutableSettings.updateSettings()
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
        val expectedModels = readFolder(Path.of("src/test/resources/examples/$testCaseName/models/"))

        val models = ModelGenerator(
            Packages(basePackage),
            sourceApi,
        ).generate()

        val sourceSet = setOf(KotlinSourceSet(models.files, Paths.get("")))
        val tempDirectory = Files.createTempDirectory("model_generator_test_${testCaseName.replace("/", ".")}")
        sourceSet.forEach {
            it.writeFileTo(tempDirectory.toFile())
        }
        val tempFolderContents = tempDirectory
            .resolve(basePackage.replace(".", File.separator))
            .resolve("models")
            .takeIf { Files.exists(it) && Files.isDirectory(it) }
            ?.let(::readFolder)
            ?: emptyMap()
        tempFolderContents.forEach {
            if (expectedModels.containsKey(it.key)) {
                assertThat((it.value))
                    .describedAs("expected model '${it.key}' does not match the given value.")
                    .isEqualTo(expectedModels[it.key])
            } else {
                assertThat(it.value).isEqualTo("File not found in expected models")
            }
        }

        tempDirectory.toFile().deleteRecursively()
    }

    @Test
    fun `generate models with suffix`() {
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
            modelSuffix = "Dto"
        )
        val basePackage = "examples.modelSuffix"
        val apiLocation = javaClass.getResource("/examples/modelSuffix/api.yaml")!!
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))
        val expectedModels = readFolder(Path.of("src/test/resources/examples/modelSuffix/models/"))

        val models = ModelGenerator(
            Packages(basePackage),
            sourceApi,
        ).generate()

        models.files.forEach { file ->
            val key = "${file.name}.kt"
            val content = file.toString()
            assertThat(content).isEqualTo(expectedModels[key])
        }
    }

    @Test
    fun `generate models using jakarta validation`() {
        val basePackage = "examples.jakartaValidationAnnotations"
        val spec = readTextResource("/examples/jakartaValidationAnnotations/api.yaml")
        val expectedJakartaModel = readTextResource("/examples/jakartaValidationAnnotations/models/Models.kt")
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
            validationLibrary = ValidationLibrary.JAKARTA_VALIDATION
        )
        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        ).generate()

        assertThat(models.files.size).isEqualTo(4)
        val validationAnnotationsModel = models.files.first { it.name == "ValidationAnnotations" }
        assertThat(validationAnnotationsModel).isNotNull
        assertThat(Linter.lintString(validationAnnotationsModel.toString())).isEqualTo(expectedJakartaModel)
    }


    @Test
    fun `generate models using no validation annotations`() {
        val basePackage = "examples.noValidationAnnotations"
        val spec = readTextResource("/examples/noValidationAnnotations/api.yaml")
        val expectedJakartaModel = readTextResource("/examples/noValidationAnnotations/models/Models.kt")
        MutableSettings.updateSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
            validationLibrary = ValidationLibrary.NO_VALIDATION
        )
        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        ).generate()

        assertThat(models.files.size).isEqualTo(4)
        val validationAnnotationsModel = models.files.first { it.name == "ValidationAnnotations" }
        assertThat(validationAnnotationsModel).isNotNull
        assertThat(Linter.lintString(validationAnnotationsModel.toString())).isEqualTo(expectedJakartaModel)
    }

    @Test
    fun `serializable models are generated from a full API definition when the java-serialized option is set`() {
        val basePackage = "examples.javaSerializableModels"
        val spec = readTextResource("/examples/javaSerializableModels/api.yaml")
        val expectedModels = readTextResource("/examples/javaSerializableModels/models/Models.kt")
        MutableSettings.updateSettings(
            modelOptions = setOf(ModelCodeGenOptionType.JAVA_SERIALIZATION),
        )
        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        )
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `missing array reference throws constructive message`() = assertExceptionWithMessage(
        "/badInput/ErrorMissingRefArray.yaml",
        "Array type 'hooks' cannot be parsed to a Schema. Check your input",
    )

    @Test
    fun `missing object reference throws constructive message`() = assertExceptionWithMessage(
        "/badInput/ErrorMissingRefObject.yaml",
        "Property 'propB' cannot be parsed to a Schema. Check your input",
    )

    @Test
    fun `mixing oneOf with object type throws constructive error`() = assertExceptionWithMessage(
        "/badInput/ErrorMixingOneOfWithObject.yaml",
        "schema contains an invalid combination of properties and `oneOf | anyOf | allOf`",
    )

    @Test
    fun `mixing anyOf with object type throws constructive error`() = assertExceptionWithMessage(
        "/badInput/ErrorMixingAnyOfWithObject.yaml",
        "schema contains an invalid combination of properties and `oneOf | anyOf | allOf`",
    )

    @Test
    fun `mixing allOf with object type throws constructive error`() = assertExceptionWithMessage(
        "/badInput/ErrorMixingAllOfWithObject.yaml",
        "schema contains an invalid combination of properties and `oneOf | anyOf | allOf`",
    )

    private fun assertExceptionWithMessage(path: String, expectedMessage: String) {
        val spec = readTextResource(path)
        val exception = assertThrows<ParameterException> {
            ModelGenerator(Packages("blah"), SourceApi(spec))
                .generate()
                .toSingleFile()
        }
        assertThat(exception.message).contains(expectedMessage)
    }

    @Test
    fun `quarkus reflection models are generated from a full API definition when the quarkus-reflection option is set`() {
        val basePackage = "examples.quarkusReflectionModels"
        val spec = readTextResource("/examples/quarkusReflectionModels/api.yaml")
        val expectedModels = readTextResource("/examples/quarkusReflectionModels/models/Models.kt")
        MutableSettings.updateSettings(
            modelOptions = setOf(ModelCodeGenOptionType.QUARKUS_REFLECTION),
        )

        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        )
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `micronaut introspected models are generated from a full API definition when the micronaut-introspection option is set`() {
        val basePackage = "examples.micronautIntrospectedModels"
        val spec = readTextResource("/examples/micronautIntrospectedModels/api.yaml")
        val expectedModels = readTextResource("/examples/micronautIntrospectedModels/models/Models.kt")
        MutableSettings.updateSettings(
            modelOptions = setOf(ModelCodeGenOptionType.MICRONAUT_INTROSPECTION),
        )

        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        )
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `micronaut reflection models are generated from a full API definition when the micronaut-reflection option is set`() {
        val basePackage = "examples.micronautReflectionModels"
        val spec = readTextResource("/examples/micronautReflectionModels/api.yaml")
        val expectedModels = readTextResource("/examples/micronautReflectionModels/models/Models.kt")
        MutableSettings.updateSettings(
            modelOptions = setOf(ModelCodeGenOptionType.MICRONAUT_REFLECTION),
        )

        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        )
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `companion object is added to models when generated from a full API definition when the companion object option is set`() {
        val basePackage = "examples.companionObject"
        val spec = readTextResource("/examples/companionObject/api.yaml")
        val expectedModels = readTextResource("/examples/companionObject/models/Models.kt")
        MutableSettings.updateSettings(
            modelOptions = setOf(ModelCodeGenOptionType.INCLUDE_COMPANION_OBJECT),
        )

        val models = ModelGenerator(
            Packages(basePackage),
            SourceApi(spec),
        )
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
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
