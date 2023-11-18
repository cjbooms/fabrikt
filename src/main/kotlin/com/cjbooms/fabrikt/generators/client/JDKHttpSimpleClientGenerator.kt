package com.cjbooms.fabrikt.generators.client

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.functionName
import com.cjbooms.fabrikt.generators.GeneratorUtils.primaryPropertiesConstructor
import com.cjbooms.fabrikt.generators.GeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.TypeFactory
import com.cjbooms.fabrikt.generators.client.ClientGeneratorUtils.ADDITIONAL_HEADERS_PARAMETER_NAME
import com.cjbooms.fabrikt.generators.client.ClientGeneratorUtils.addIncomingParameters
import com.cjbooms.fabrikt.generators.client.ClientGeneratorUtils.deriveClientParameters
import com.cjbooms.fabrikt.generators.client.ClientGeneratorUtils.simpleClientName
import com.cjbooms.fabrikt.generators.client.ClientGeneratorUtils.toClientReturnType
import com.cjbooms.fabrikt.model.*
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.javaparser.utils.CodeGenerationUtils
import com.reprezen.kaizen.oasparser.model3.Operation
import com.squareup.kotlinpoet.*
import java.nio.file.Path

class JDKHttpSimpleClientGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val srcPath: Path = Destinations.MAIN_KT_SOURCE
) {

    fun generateDynamicClientCode(): Collection<ClientType> {
        return api.openApi3.routeToPaths().map { (resourceName, paths) ->
            val funcSpecs: List<FunSpec> = paths.flatMap { (resource, path) ->
                path.operations.map { (verb, operation) ->
                    val parameters = deriveClientParameters(path, operation, packages.base)
                    FunSpec
                        .builder(functionName(operation, resource, verb))
                        .addModifiers(KModifier.PUBLIC)
                        .addKdoc(operation.toKdoc(parameters))
                        .addAnnotation(
                            AnnotationSpec.builder(Throws::class)
                                .addMember("%T::class", "ApiException".toClassName(packages.client)).build()
                        )
                        .addIncomingParameters(parameters)
                        .addParameter(
                            ParameterSpec.builder(
                                ADDITIONAL_HEADERS_PARAMETER_NAME,
                                TypeFactory.createMapOfStringToType(String::class.asTypeName())
                            )
                                .defaultValue("emptyMap()")
                                .build()
                        )
                        .addCode(
                            SimpleJDKClientOperationStatement(
                                packages,
                                resource,
                                verb,
                                operation,
                                parameters,
                            ).toStatement()
                        )
                        .returns(operation.toClientReturnType(packages))
                        .build()
                }
            }

            val clientType = TypeSpec.classBuilder(simpleClientName(resourceName))
                .primaryPropertiesConstructor(
                    PropertySpec.builder("objectMapper", ObjectMapper::class.asTypeName(), KModifier.PRIVATE).build(),
                    PropertySpec.builder("baseUrl", String::class.asTypeName(), KModifier.PRIVATE).build(),
                    PropertySpec.builder("client", "HttpClient".toClassName("java.net.http"), KModifier.PRIVATE).build()
                )
                .addAnnotation(AnnotationSpec.builder(Suppress::class).addMember("%S", "unused").build())
                .addFunctions(funcSpecs)
                .build()

            ClientType(clientType, packages.base)
        }.toSet()
    }

    fun generateLibrary(): Collection<GeneratedFile> {
        val codeDir = srcPath.resolve(CodeGenerationUtils.packageToPath(packages.base))
        val clientDir = codeDir.resolve("client")
        return setOf(
            /*HandlebarsTemplates.applyTemplate(
                template = HandlebarsTemplates.clientApiModels,
                input = packages,
                path = clientDir,
                fileName = "ApiModels.kt"
            ),*/
            HandlebarsTemplates.applyTemplate(
                template = HandlebarsTemplates.clientJDKHttpUtils,
                input = packages,
                path = clientDir,
                fileName = "HttpUtil.kt"
            ),
            /*HandlebarsTemplates.applyTemplate(
                template = HandlebarsTemplates.clientOAuth,
                input = packages,
                path = clientDir,
                fileName = "OAuth.kt"
            )*/
        )
    }
}

data class SimpleJDKClientOperationStatement(
    private val packages: Packages,
    private val resource: String,
    private val verb: String,
    private val operation: Operation,
    private val parameters: List<IncomingParameter>
) {
    private val basePackage = "java.net"
    private val httpBasePackage = "java.net.http"
    fun toStatement(): CodeBlock =
        CodeBlock.builder()
            .addUrlStatement()
            /*.addPathParamStatement()
            .addQueryParamStatement()*/
            .addRequestStatement()
            .addHeaderParamStatement()
            .addRequestExecutionStatement()
            .build()

    private fun CodeBlock.Builder.addUrlStatement(): CodeBlock.Builder {
        this.add("val httpUri: %T = URI.create(\"%L\")", "URI".toClassName(basePackage), "\$baseUrl$resource")
        return this
    }

    private fun CodeBlock.Builder.addRequestStatement(): CodeBlock.Builder {
        this.add("\nval requestBuilder: %T.Builder = HttpRequest.newBuilder()", "HttpRequest".toClassName(httpBasePackage))
        this.add("\n.uri(httpUri)")

        /*when (val op = verb.toUpperCase()) {
            "PUT" -> this.addRequestSerializerStatement("put")
            "POST" -> this.addRequestSerializerStatement("post")
            "PATCH" -> this.addRequestSerializerStatement("patch")
            "HEAD" -> this.add("\n.head()")
            "GET" -> this.add("\n.get()")
            "DELETE" -> this.add("\n.delete()")
            else -> throw NotImplementedError("API operation $op is not supported")
        }*/
        return this
    }

    private fun CodeBlock.Builder.addRequestExecutionStatement() =
        this.add("\nreturn client.execute(requestBuilder.build())\n")

    private fun CodeBlock.Builder.addHeaderParamStatement(): CodeBlock.Builder {
        return this.add("\nadditionalHeaders.forEach { requestBuilder.header(it.key, it.value) }")
    }
}