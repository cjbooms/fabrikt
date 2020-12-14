package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.validation.Linter
import com.squareup.kotlinpoet.FileSpec
import java.nio.file.Paths
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModelGeneratorTest {

    private fun testCases(): Stream<String> = Stream.of(
        "optionalVsRequired",
        "snakeToCamel",
        "arraySimpleInLined",
        "objectRef",
        "inLinedObject",
        "arrayRef",
        "simpleRef",
        "allOf",
        "anyOf",
        "requiredReadOnly",
        "polymorphicModels",
        "oneOfPolymorphicModels",
        "validationAnnotations",
        "wildCardTypes",
        "nullableListTypes",
        "oneOfAndTopLevelProps",
        "oneOf",
        "complexAnyOneAllCombo",
        "duplicatePropertyHandling",
        "allOfNestedOneOf",
        "deepNestedReferences",
        "sharingObjectDefinitions",
        "deepNestedSharingReferences",
        "mixingCamelSnakeLispCase",
        "mapExamples",
        "enumExamples",
        "arrayOfArrays",
        "defaultValues",
        "enumPolymorphicDiscriminator",
        "externalReferences",
        "githubApi"
    )

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        print("Testcase: $testCaseName")
        val basePackage = "examples.$testCaseName"
        val apiLocation = javaClass.getResource("/examples/$testCaseName/api.yaml")
        val sourceApi = SourceApi(apiLocation.readText(), baseDir = Paths.get(apiLocation.toURI()))
        val expectedModels = javaClass.getResource("/examples/$testCaseName/models/Models.kt").readText()

        val models = JacksonModelGenerator(
            Packages(basePackage),
            sourceApi
        ).generate().toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `sealed classes are correctly grouped into a single file`() {
        val basePackage = "examples.polymorphicModels"
        val spec = javaClass.getResource("/examples/polymorphicModels/api.yaml").readText()
        val expectedModels = javaClass.getResource("/examples/polymorphicModels/models/Models.kt").readText()

        val models = JacksonModelGenerator(
            Packages(basePackage),
            SourceApi(spec)
        ).generate()

        assertThat(Linter.lintString(models.files.first().toString())).isEqualTo(expectedModels)
    }

    @Test
    fun `serializable models are generated from a full API definition when the java-serialized option is set`() {
        val basePackage = "examples.javaSerializableModels"
        val spec = javaClass.getResource("/examples/javaSerializableModels/api.yaml").readText()
        val expectedModels = javaClass.getResource("/examples/javaSerializableModels/models/Models.kt").readText()

        val models = JacksonModelGenerator(Packages(basePackage), SourceApi(spec), setOf(ModelCodeGenOptionType.JAVA_SERIALIZATION))
            .generate()
            .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
    }

    @Test
    fun `quarkus reflection models are generated from a full API definition when the quarkus-reflection option is set`() {
        val basePackage = "examples.quarkusReflectionModels"
        val spec = javaClass.getResource("/examples/quarkusReflectionModels/api.yaml").readText()
        val expectedModels = javaClass.getResource("/examples/quarkusReflectionModels/models/Models.kt").readText()

        val models = JacksonModelGenerator(Packages(basePackage), SourceApi(spec), setOf(ModelCodeGenOptionType.QUARKUS_REFLECTION))
                .generate()
                .toSingleFile()

        assertThat(models).isEqualTo(expectedModels)
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
