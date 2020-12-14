package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.SpringControllerGenerator
import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.service.SpringServiceInterfaceGenerator
import com.cjbooms.fabrikt.model.Controllers
import com.cjbooms.fabrikt.model.Destinations.controllersPackage
import com.cjbooms.fabrikt.model.Destinations.servicesPackage
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.validation.Linter
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringControllerGeneratorTest {
    private val basePackage = "ie.zalando"
    private lateinit var generated: Collection<FileSpec>

    private fun testCases(): Stream<String> = Stream.of(
        "githubApi"
    )

    private fun setupGithubApiTestEnv() {
        val api = SourceApi(javaClass.getResource("/examples/githubApi/api.yaml").readText())
        val models = JacksonModelGenerator(Packages(basePackage), api).generate().models
        val services = SpringServiceInterfaceGenerator(Packages(basePackage), api, models).generate().services
        generated = SpringControllerGenerator(Packages(basePackage), api, services, models).generate().files
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
                "RepositoriesPullRequestsController"
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
            "org.springframework.web.bind.annotation.RequestMapping"
        )
    }

    @Test
    fun `ensure controller has correct service parameter`() {
        setupGithubApiTestEnv()
        val controllerServices =
            generated.flatMap { it.members.flatMap { (it as TypeSpec).propertySpecs.map { it.type.toString() } } }
        val servicePackage = servicesPackage(basePackage)
        assertThat(controllerServices).containsOnly(
            "$servicePackage.InternalEventsService",
            "$servicePackage.ContributorsService",
            "$servicePackage.OrganisationsService",
            "$servicePackage.OrganisationsContributorsService",
            "$servicePackage.RepositoriesService",
            "$servicePackage.RepositoriesPullRequestsService"
        )
    }

    @Test
    fun `ensure that subresource specific controllers are created`() {
        val api = SourceApi(javaClass.getResource("/examples/githubApi/api.yaml").readText())
        val models = JacksonModelGenerator(Packages(basePackage), api).generate().models
        val services = SpringServiceInterfaceGenerator(Packages(basePackage), api, models).generate().services
        val controllers = SpringControllerGenerator(Packages(basePackage), api, services, models).generate()

        assertThat(controllers.files).size().isEqualTo(6)
        assertThat(controllers.files.map { it.name }).containsAll(
            listOf(
                "ContributorsController",
                "OrganisationsController",
                "OrganisationsContributorsController",
                "RepositoriesController",
                "RepositoriesPullRequestsController"
            )
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
            "deleteById"
        )
        assertThat(ownedFunctionNames).containsExactlyInAnyOrder(
            "get",
            "getById",
            "post",
            "putById"
        )
    }

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct models are generated for different OpenApi Specifications`(testCaseName: String) {
        val basePackage = "examples.$testCaseName"
        val api = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())
        val expectedControllers = javaClass.getResource("/examples/$testCaseName/controllers/Controllers.kt").readText()

        val models = JacksonModelGenerator(Packages(basePackage), api).generate().models
        val services = SpringServiceInterfaceGenerator(Packages(basePackage), api, models).generate().services
        val controllers = SpringControllerGenerator(
            Packages(basePackage),
            api,
            services,
            models
        ).generate().toSingleFile()

        assertThat(controllers).isEqualTo(expectedControllers)
    }

    private fun Controllers.toSingleFile(): String {
        val destPackage = if (controllers.isNotEmpty()) controllers.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        controllers.forEach {
            val builder = singleFileBuilder
                .addImport(SpringImports.Static.REQUEST_METHOD.first, SpringImports.Static.REQUEST_METHOD.second)
                .addImport(
                    SpringImports.Static.RESPONSE_STATUS.first,
                    SpringImports.Static.RESPONSE_STATUS.second
                )
                .addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }
}
