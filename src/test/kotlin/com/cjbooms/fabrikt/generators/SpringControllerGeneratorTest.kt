package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.SpringControllerInterfaceGenerator
import com.cjbooms.fabrikt.generators.controller.SpringControllers
import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
import com.cjbooms.fabrikt.model.Destinations.controllersPackage
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.Linter
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringControllerGeneratorTest {
    private val basePackage = "ie.zalando"
    private lateinit var generated: Collection<FileSpec>

    @Suppress("unused")
    private fun testCases(): Stream<String> = Stream.of(
        "githubApi",
        "singleAllOf",
        "pathLevelParameters",
        "parameterNameClash",
        "responsesSchema",
        "requestsSchema",
    )

    private fun setupGithubApiTestEnv() {
        val api = SourceApi(readTextResource("/examples/githubApi/api.yaml"))
        generated = SpringControllerInterfaceGenerator(Packages(basePackage), api).generate().files
    }

    @BeforeEach
    fun init() {
        MutableSettings.updateSettings(genTypes = setOf(CodeGenerationType.CONTROLLERS))
    }

    // @Test
    // fun `debug single test`() = `correct models are generated for different OpenApi Specifications`("singleAllOf")

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        val basePackage = "examples.$testCaseName"
        val api = SourceApi(readTextResource("/examples/$testCaseName/api.yaml"))
        val expectedControllers = readTextResource("/examples/$testCaseName/controllers/spring/Controllers.kt")

        val controllers = SpringControllerInterfaceGenerator(
            Packages(basePackage),
            api,
        ).generate().toSingleFile()

        assertThat(controllers).isEqualTo(expectedControllers)
    }

    @Test
    fun `correct models are generated for ControllerCodeGenOptionType_AUTHENTICATION`() {
        val basePackage = "examples.authentication"
        val api = SourceApi(readTextResource("/examples/authentication/api.yaml"))
        val expectedControllers = readTextResource("/examples/authentication/controllers/spring/Controllers.kt")

        val controllers = SpringControllerInterfaceGenerator(
            Packages(basePackage),
            api,
            setOf(ControllerCodeGenOptionType.AUTHENTICATION),
        ).generate().toSingleFile()

        assertThat(controllers).isEqualTo(expectedControllers)
    }

    @Test
    fun `should contain correct number of controller classes`() {
        setupGithubApiTestEnv()
        assertThat(generated.size).isEqualTo(6)
    }

    @Test
    fun `the correct controllers are generated`() {
        setupGithubApiTestEnv()
        assertThat(generated.map { it.name })
            .containsOnly(
                "InternalEventsController",
                "ContributorsController",
                "OrganisationsController",
                "OrganisationsContributorsController",
                "RepositoriesController",
                "RepositoriesPullRequestsController",
            )
    }

    @Test
    fun `ensure package is correct`() {
        setupGithubApiTestEnv()
        assertThat(generated.map { it.packageName }.distinct())
            .containsOnly(controllersPackage(basePackage))
    }

    @Test
    fun `ensure controller has correct annotations`() {
        setupGithubApiTestEnv()
        val controllerAnnotations =
            generated.flatMap { it.members.flatMap { (it as TypeSpec).annotationSpecs.map { it.className.toString() } } }
                .distinct()

        assertThat(controllerAnnotations).containsOnly(
            "org.springframework.stereotype.Controller",
            "org.springframework.validation.annotation.Validated",
            "org.springframework.web.bind.annotation.RequestMapping",
        )
    }

    @Test
    fun `ensure that subresource specific controllers are created`() {
        val api = SourceApi(readTextResource("/examples/githubApi/api.yaml"))
        val controllers = SpringControllerInterfaceGenerator(Packages(basePackage), api).generate()

        assertThat(controllers.files).size().isEqualTo(6)
        assertThat(controllers.files.map { it.name }).containsAll(
            listOf(
                "ContributorsController",
                "OrganisationsController",
                "OrganisationsContributorsController",
                "RepositoriesController",
                "RepositoriesPullRequestsController",
            ),
        )

        val linkedFunctionNames = controllers.files
            .filter { it.name == "OrganisationsContributorsController" }
            .flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.map { it.name } }
        val ownedFunctionNames = controllers.files
            .filter { it.name == "RepositoriesPullRequestsController" }
            .flatMap { it.members }
            .flatMap { (it as TypeSpec).funSpecs.map { it.name } }
        assertThat(linkedFunctionNames).containsExactlyInAnyOrder(
            "get",
            "getById",
            "putById",
            "deleteById",
        )
        assertThat(ownedFunctionNames).containsExactlyInAnyOrder(
            "get",
            "getById",
            "post",
            "putById",
        )
    }

    @Test
    fun `ensure controller methods has the correct modifiers`() {
        val api = SourceApi(readTextResource("/examples/githubApi/api.yaml"))
        val controllers = SpringControllerInterfaceGenerator(
            Packages(basePackage),
            api,
            setOf(ControllerCodeGenOptionType.SUSPEND_MODIFIER),
        ).generate()

        assertThat(controllers.files).size().isEqualTo(6)
        assertThat(
            controllers.files
                .flatMap { file -> file.members }
                .flatMap { (it as TypeSpec).funSpecs.map(FunSpec::modifiers) }
                .all { it.contains(KModifier.SUSPEND) },
        ).isTrue()
    }

    private fun SpringControllers.toSingleFile(): String {
        val destPackage = if (controllers.isNotEmpty()) controllers.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        controllers.forEach {
            val builder = singleFileBuilder
                .addImport(SpringImports.Static.REQUEST_METHOD.first, SpringImports.Static.REQUEST_METHOD.second)
                .addImport(
                    SpringImports.Static.RESPONSE_STATUS.first,
                    SpringImports.Static.RESPONSE_STATUS.second,
                )
                .addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }

    @Test
    fun `controller parameters should have spring DateTimeFormat annotations`() {
        val api = SourceApi(readTextResource("/examples/springFormatDateAndDateTime/api.yaml"))
        val controllers = SpringControllerInterfaceGenerator(Packages(basePackage), api).generate().toSingleFile()
        val expectedControllers = readTextResource("/examples/springFormatDateAndDateTime/controllers/Controllers.kt")

        assertThat(controllers.trim()).isEqualTo(expectedControllers.trim())
    }
}
