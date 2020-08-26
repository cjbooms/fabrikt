package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.service.SpringServiceInterfaceGenerator
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.Destinations.servicesPackage
import com.cjbooms.fabrikt.model.Services
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.OperationUtils
import com.cjbooms.fabrikt.util.SupportedOperation
import com.cjbooms.fabrikt.validation.Linter
import com.squareup.kotlinpoet.FileSpec
import java.util.stream.Stream
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringServiceInterfaceGeneratorTest {

    val api = SourceApi(javaClass.getResource("/examples/githubApi/api.yaml").readText())

    private fun testCases(): Stream<String> = Stream.of(
        "putApi",
        "simpleRequestBody",
        "customerExampleApi"
    )

    private val basePackage = "testdata"

    @Test
    fun `the correct services are generated`() {
        val models = JacksonModelGenerator(Packages(basePackage), api).generate().models

        val generated = SpringServiceInterfaceGenerator(Packages(basePackage), api, models).generate().files

        assertThat(generated.map { it.name })
            .containsOnly(
                "ContributorsService",
                "OrganisationsService",
                "OrganisationsContributorsService",
                "RepositoriesService",
                "RepositoriesPullRequestsService"
            )
    }

    @Test
    fun `ensure package is correct`() {
        val models = JacksonModelGenerator(Packages(basePackage), api).generate().models

        val generated = SpringServiceInterfaceGenerator(Packages(basePackage), api, models).generate().files

        assertThat(generated.map { it.packageName }.distinct())
            .containsOnly(servicesPackage(basePackage))
    }

    private fun supportedOperationFrom(verb: String, path: String, apiSeed: SourceApi): SupportedOperation? {
        val operation = apiSeed.openApi3.paths.flatMap { p ->
            p.value.operations.map { o ->
                if (p.key == path && o.key == verb) o.value
                else null
            }
        }.filterNotNull().first()

        return OperationUtils.supportedOperationFrom(verb, operation, apiSeed.openApi3.paths[path]!!, apiSeed)
    }

    @Test
    fun `test service operations map correctly`() {
        val apiSeed = SourceApi(javaClass.getResource("/examples/githubApi/api.yaml").readText())

        assertThat(supportedOperationFrom("get", "/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.READ)
        assertThat(supportedOperationFrom("head", "/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.READ)
        assertThat(supportedOperationFrom("put", "/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.UPDATE)
        assertThat(supportedOperationFrom("get", "/contributors", apiSeed))
            .isEqualTo(SupportedOperation.QUERY)
        assertThat(supportedOperationFrom("post", "/contributors", apiSeed))
            .isEqualTo(SupportedOperation.CREATE)

        assertThat(supportedOperationFrom("post", "/repositories/{parent-id}/pull-requests", apiSeed))
            .isEqualTo(SupportedOperation.CREATE_SUBRESOURCE)
        assertThat(supportedOperationFrom("get", "/repositories/{parent-id}/pull-requests", apiSeed))
            .isEqualTo(SupportedOperation.QUERY_SUBRESOURCE)
        assertThat(supportedOperationFrom("put", "/repositories/{parent-id}/pull-requests/{id}", apiSeed))
            .isEqualTo(SupportedOperation.UPDATE_SUBRESOURCE)
        assertThat(supportedOperationFrom("get", "/repositories/{parent-id}/pull-requests/{id}", apiSeed))
            .isEqualTo(SupportedOperation.READ_SUBRESOURCE)

        assertThat(supportedOperationFrom("put", "/organisations/{parent-id}/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.ADD_TOP_LEVEL_SUBRESOURCE)
        assertThat(supportedOperationFrom("get", "/organisations/{parent-id}/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.READ_TOP_LEVEL_SUBRESOURCE)
        assertThat(supportedOperationFrom("get", "/organisations/{parent-id}/contributors", apiSeed))
            .isEqualTo(SupportedOperation.QUERY_TOP_LEVEL_SUBRESOURCE)
        assertThat(supportedOperationFrom("delete", "/organisations/{parent-id}/contributors/{id}", apiSeed))
            .isEqualTo(SupportedOperation.REMOVE_TOP_LEVEL_SUBRESOURCE)
    }

    @Test
    fun `test method signatures`() {
        val spec = """
            openapi: "3.0.0"
            paths:
              /contributors:
                get:
                  parameters:
                  - ${'$'}ref: "#/components/parameters/FlowId"
                  - ${'$'}ref: "#/components/parameters/Cursor"
                  responses:
                    200:
                      content:
                        application/json:
                          schema:
                            ${'$'}ref: "#/components/schemas/ContributorQueryResult"
                post:
                  parameters:
                  - ${'$'}ref: "#/components/parameters/FlowId"
                  requestBody:
                    ${'$'}ref: "#/components/requestBodies/ContributorBody"
                  responses:
                    201:
                      description: ""

              /contributors/{id}:
                get:
                  parameters:
                  - ${'$'}ref: "#/components/parameters/FlowId"
                  - ${'$'}ref: "#/components/parameters/IdPathParam"
                  responses:
                    200:
                      content:
                        application/json:
                          schema:
                            ${'$'}ref: "#/components/schemas/Contributor"
                head:
                  parameters:
                  - ${'$'}ref: "#/components/parameters/FlowId"
                  - ${'$'}ref: "#/components/parameters/IdPathParam"
                  responses:
                    200:
                      description: ""
                put:
                  parameters:
                  - ${'$'}ref: "#/components/parameters/FlowId"
                  - ${'$'}ref: "#/components/parameters/IdPathParam"
                  requestBody:
                    ${'$'}ref: "#/components/requestBodies/ContributorBody"
                  responses:
                    204:
                      description: ""

            components:
              parameters:
                IdPathParam:
                  name: "id"
                  in: "path"
                  required: true
                  schema:
                    type: "string"
                FlowId:
                  name: "X-Flow-Id"
                  in: "header"
                  required: false
                  schema:
                    type: "string"
                Cursor:
                  name: "cursor"
                  in: "query"
                  required: false
                  schema:
                    type: "string"
              headers:
                Location:
                  schema:
                    type: "string"
              requestBodies:
                ContributorBody:
                  required: true
                  content:
                    application/json:
                      schema:
                        ${'$'}ref: "#/components/schemas/Contributor"
              schemas:
                ContributorQueryResult:
                  type: "object"
                  required:
                  - "items"
                  properties:
                    items:
                      type: "array"
                      minItems: 0
                      items:
                        ${'$'}ref: "#/components/schemas/Contributor"
                Contributor:
                  type: "object"
                  properties:
                    id:
                      type: "string"
                      readOnly: true
                    username:
                      type: "string"
                    name:
                      type: "string"""".trimIndent()

        val apiSeed = SourceApi(spec)
        val models = JacksonModelGenerator(Packages(basePackage), apiSeed).generate().models

        val methods =
            apiSeed.openApi3.paths.flatMap {
                SpringServiceInterfaceGenerator(
                    Packages(basePackage),
                    apiSeed,
                    models
                ).pathToMethods(it.value)
            }

        val list = methods.first { it.name == "query" }
        val create = methods.first { it.name == "create" }
        val update = methods.first { it.name == "update" }
        val get = methods.first { it.name == "read" }

        val modelsPackage = modelsPackage(basePackage)
        assertThat(list.returnType.toString()).isEqualTo("$modelsPackage.ContributorQueryResult")
        assertThat(create.returnType.toString()).isEqualTo("kotlin.Pair<java.net.URI, $modelsPackage.Contributor?>")
        assertThat(get.returnType.toString()).isEqualTo("$modelsPackage.Contributor")
        assertThat(update.returnType.toString()).isEqualTo("$modelsPackage.Contributor")

        assertThat(list.parameters.map { it.name }).containsExactly("xFlowId", "cursor")
        assertThat(create.parameters.map { it.name }).containsExactly("contributor", "xFlowId")
        assertThat(get.parameters.map { it.name }).containsExactly("id", "xFlowId")
        assertThat(update.parameters.map { it.name }).containsExactly("contributor", "id", "xFlowId")
    }

    @ParameterizedTest
    @MethodSource("testCases")
    fun `correct services are generated for different OpenApi Specifications`(testCaseName: String) {
        val basePackage = "examples.${testCaseName.decapitalize()}"
        val api = SourceApi(javaClass.getResource("/examples/$testCaseName/api.yaml").readText())
        val expectedServices = javaClass.getResource("/examples/$testCaseName/service/Services.kt").readText()
        val jackson = JacksonModelGenerator(Packages(basePackage), api).generate()

        val services = SpringServiceInterfaceGenerator(Packages(basePackage), api, jackson.models).generate()

        assertThat(services.toSingleFile()).isEqualTo(expectedServices)
    }

    @Test
    fun `schema only code gen for Service Interfaces is not Empty`() {
        val rawApiSeed = SourceApi(javaClass.getResource("/examples/schemaOnlyApi/api.yaml").readText())
        val models = JacksonModelGenerator(Packages(basePackage), rawApiSeed).generate().models

        val generated = SpringServiceInterfaceGenerator(Packages(basePackage), rawApiSeed, models).generate().files

        assertThat(generated.isNotEmpty())
    }

    private fun Services.toSingleFile(): String {
        val destPackage = if (services.isNotEmpty()) services.first().destinationPackage else ""
        val singleFileBuilder = FileSpec.builder(destPackage, "dummyFilename")
        services.forEach {
            val builder = singleFileBuilder.addType(it.spec)
            builder.build()
        }
        return Linter.lintString(singleFileBuilder.build().toString())
    }
}
