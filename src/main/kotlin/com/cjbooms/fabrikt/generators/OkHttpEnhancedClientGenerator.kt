package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.configurations.PackagesConfig
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.enhancedClientName
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.functionName
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.simpleClientName
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.toBodyParameterSpec
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.toKCodeName
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.toParameterSpec
import com.cjbooms.fabrikt.generators.ClientGeneratorUtils.toReturnType
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.Destinations
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.HandlebarsTemplates
import com.cjbooms.fabrikt.model.SimpleFile
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javaparser.utils.CodeGenerationUtils
import com.reprezen.kaizen.oasparser.model3.Operation
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

class OkHttpEnhancedClientGenerator(
    private val config: PackagesConfig,
    private val api: SourceApi
) {

    fun generateDynamicClientCode(options: Set<ClientCodeGenOptionType>): Collection<ClientType> =
        options.ifResilience4jIsEnabled {
            generateResilience4jClientCode()
        }

    private fun generateResilience4jClientCode(): Collection<ClientType> {
        return api.openApi3.routeToPaths().map { (resourceName, paths) ->
            val funSpecs: List<FunSpec> = paths.flatMap { (resource, path) ->
                path.operations.map { (verb, operation) ->
                    FunSpec
                        .builder(functionName(resource, verb))
                        .addModifiers(KModifier.PUBLIC)
                        .addAnnotation(
                            AnnotationSpec.builder(Throws::class)
                                .addMember("%T::class", "ApiException".toClassName(config.packages.client)).build()
                        )
                        .addParameters(operation.requestBody.toBodyParameterSpec(config.packages.base))
                        .addParameters(operation.parameters.map { it.toParameterSpec(config.packages.base) })
                        .addCode(Resilience4jClientOperationStatement(config, resource, verb, operation).toStatement())
                        .returns(operation.toReturnType(config))
                        .build()
                }
            }

            generateCircuitBreakerClientCode(resourceName, funSpecs)
        }.toSet()
    }

    private fun generateCircuitBreakerClientCode(resourceName: String, funSpecs: List<FunSpec>): ClientType {
        val apiClientClassName = simpleClientName(resourceName).toClassName(config.packages.client)
        val circuitBreakerRegistryClassName =
            "CircuitBreakerRegistry".toClassName("io.github.resilience4j.circuitbreaker")

        val configurableCircuitBreakerNameProperty = PropertySpec.builder("circuitBreakerName", String::class.asTypeName())
            .mutable()
            .initializer("%S", apiClientClassName.simpleName.toKCodeName())
            .build()

        val clientProperty = PropertySpec.builder("apiClient", apiClientClassName, KModifier.PRIVATE)
            .initializer("%T(objectMapper, baseUrl, client)", apiClientClassName)
            .build()

        val circuitBreakerRegistryProperty =
            PropertySpec.builder("circuitBreakerRegistry", circuitBreakerRegistryClassName, KModifier.PRIVATE)
                .initializer("circuitBreakerRegistry")
                .build()

        val constructor = FunSpec.constructorBuilder()
            .addParameters(
                listOf(
                    ParameterSpec.builder(
                        circuitBreakerRegistryProperty.name,
                        circuitBreakerRegistryProperty.type
                    ).build(),
                    ParameterSpec.builder("objectMapper", ObjectMapper::class.asTypeName()).build(),
                    ParameterSpec.builder("baseUrl", String::class.asTypeName()).build(),
                    ParameterSpec.builder("client", "OkHttpClient".toClassName("okhttp3")).build()
                )
            ).build()

        val clientType = TypeSpec.classBuilder(enhancedClientName(resourceName))
            .addKdoc(addClientKDoc())
            .primaryConstructor(constructor)
            .addProperty(circuitBreakerRegistryProperty)
            .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "unused").build())
            .addProperty(configurableCircuitBreakerNameProperty)
            .addProperty(clientProperty)
            .addFunctions(funSpecs)
            .build()

        return ClientType(clientType, config.packages.base)
    }

    fun generateLibrary(options: Set<ClientCodeGenOptionType>): Collection<GeneratedFile> {
        val codeDir = Destinations.MAIN_KT_SRC.resolve(CodeGenerationUtils.packageToPath(config.packages.base))
        val clientDir = codeDir.resolve("client")
        return listOfNotNull(
            applyTemplateIfOptionIsEnabled(options, ClientCodeGenOptionType.RESILIENCE4J) {
                HandlebarsTemplates.applyTemplate(
                    HandlebarsTemplates.clientHttpResilience4jUtils,
                    config,
                    clientDir,
                    "HttpResilience4jUtil.kt"
                )
            }
        )
    }

    private fun addClientKDoc(): CodeBlock =
        CodeBlock.builder()
            .add("The circuit breaker registry should have the proper configuration to correctly action on circuit breaker ")
            .add("transitions based on the client exceptions [ApiClientException], [ApiServerException] and [IOException].\n")
            .add("\n@see ApiClientException")
            .add("\n@see ApiServerException")
            .build()

    private fun applyTemplateIfOptionIsEnabled(
        options: Set<ClientCodeGenOptionType>,
        target: ClientCodeGenOptionType,
        applyTemplate: () -> SimpleFile
    ): SimpleFile? =
        options.find { it == target }?.let { applyTemplate() }

    private fun Set<ClientCodeGenOptionType>.ifResilience4jIsEnabled(
        block: (Set<ClientCodeGenOptionType>) -> Collection<ClientType>
    ): Collection<ClientType> =
        if (this.any { it == ClientCodeGenOptionType.RESILIENCE4J }) block(this) else emptySet()
}

class Resilience4jClientOperationStatement(
    private val config: PackagesConfig,
    private val resource: String,
    private val verb: String,
    private val operation: Operation
) {
    fun toStatement(): CodeBlock =
        CodeBlock.builder()
            .addCircuitBreakerStatement()
            .build()

    private fun CodeBlock.Builder.addCircuitBreakerStatement(): CodeBlock.Builder {
        this.add("return \n withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {\n")
        this.addClientCallStatement()
        this.add("\n}\n")
        return this
    }

    private fun CodeBlock.Builder.addClientCallStatement(): CodeBlock.Builder {
        val params = operation.requestBody.toBodyParameterSpec(config.packages.base).plus(
            operation.parameters.map { it.toParameterSpec(config.packages.base) }
        )
        this.add("apiClient.%N(%L)", functionName(resource, verb), params.joinToString(",") { it.name })
        return this
    }
}
