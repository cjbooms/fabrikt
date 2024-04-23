package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.SecuritySupport
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.happyPathResponse
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.securitySupport
import com.cjbooms.fabrikt.model.BodyParameter
import com.cjbooms.fabrikt.model.ControllerLibraryType
import com.cjbooms.fabrikt.model.ControllerType
import com.cjbooms.fabrikt.model.HeaderParam
import com.cjbooms.fabrikt.model.IncomingParameter
import com.cjbooms.fabrikt.model.KotlinTypes
import com.cjbooms.fabrikt.model.PathParam
import com.cjbooms.fabrikt.model.QueryParam
import com.cjbooms.fabrikt.model.RequestParameter
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.cjbooms.fabrikt.util.NormalisedString.camelCase
import com.cjbooms.fabrikt.util.toUpperCase
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName

private const val CONTROLLER_RESULT_CLASS_NAME = "ControllerResult"

/**
 * Generates controller interface and routing functions for Ktor.
 *
 * Ktor in its nature is very flexible and imposes very few constraints on how you structure your application. In order
 * to be able to generate code we however have to add structure.
 *
 * This generator creates a routing function (as an extension function on Ktor's Route class) which you use to mount
 * the route on your Ktor server. The route function handles basic parsing of parameters and delegates the actual
 * handling of the request to a controller interface which you implement.
 */
class KtorControllerInterfaceGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val options: Set<ControllerCodeGenOptionType> = emptySet(),
) : ControllerInterfaceGenerator {
    override fun generate(): KtorControllers {
        val controllerInterfaces = api.openApi3.routeToPaths().map { (resourceName, paths) ->
            val controllerBuilder = TypeSpec.interfaceBuilder(ControllerGeneratorUtils.controllerName(resourceName))

            val routeFunBuilder = FunSpec.builder("${resourceName.camelCase()}Routes")
                .receiver(ClassName("io.ktor.server.routing", "Route"))
                .addParameter(
                    "controller",
                    ClassName(packages.controllers, ControllerGeneratorUtils.controllerName(resourceName))
                )

            paths.forEach { path ->
                path.value.operations
                    .filter { (verb, _) -> verb.toUpperCase() != "HEAD" }
                    .forEach { (verb, operation) ->
                        // add route handler
                        val routeCode = buildRouteCode(operation, verb, path)
                        routeFunBuilder.addCode(routeCode)

                        // generate controller interface
                        val methodName = getMethodName(operation, verb, path)
                        val controllerFunBuilder = FunSpec.builder(methodName)
                            .addModifiers(setOf(KModifier.ABSTRACT, KModifier.SUSPEND))
                            .addParameter("call", ClassName("io.ktor.server.application", "ApplicationCall"))

                        val happyPathResponse = operation.happyPathResponse(packages.base)
                        if (happyPathResponse.simpleName() != Unit::class.simpleName) {
                            // When return type is not Unit the controller implementation is expected to return a
                            // ControllerResult, which we unpack in the routing function
                            controllerFunBuilder.returns(
                                ClassName(packages.controllers, CONTROLLER_RESULT_CLASS_NAME)
                                    .parameterizedBy(happyPathResponse)
                            )
                        }

                        val params = operation.toIncomingParameters(packages.base, path.value.parameters, emptyList())
                        val (pathParams, queryParams, headerParams, bodyParams) = params.splitByType()

                        headerParams.forEach { param ->
                            // TODO: Consider handling header parameter types. Currently everything is a string.
                            if (param.isRequired) {
                                controllerFunBuilder.addParameter(ParameterSpec.builder(param.name, String::class).build())
                            } else {
                                controllerFunBuilder.addParameter(
                                    ParameterSpec.builder(param.name, String::class.asTypeName().copy(nullable = true)).build()
                                )
                            }
                        }

                        (pathParams + queryParams).forEach { param ->
                            if (param.isRequired) {
                                controllerFunBuilder.addParameter(param.toParameterSpecBuilder().build())
                            } else {
                                controllerFunBuilder.addParameter(
                                    ParameterSpec.builder(param.name, param.type.copy(nullable = true)).build()
                                )
                            }
                        }

                        bodyParams.forEach { param ->
                            controllerFunBuilder.addParameter(param.toParameterSpecBuilder().build())
                        }

                        val securityOption = operation.securitySupport(globalSecurity)
                        val addAuth = securityOption.allowsAuthenticated && options.contains(ControllerCodeGenOptionType.AUTHENTICATION)
                        if (addAuth) {
                            controllerFunBuilder.addParameter(
                                "principal",
                                ClassName(
                                    "io.ktor.server.auth",
                                    "Principal"
                                ).copy(nullable = securityOption == SecuritySupport.AUTHENTICATION_OPTIONAL)
                            ).build()
                        }

                        controllerFunBuilder.addKdoc(operation.toKdoc(params))

                        controllerBuilder.addFunction(controllerFunBuilder.build())
                    }
            }

            // add the companion object with the route functions
            controllerBuilder.addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(routeFunBuilder.build())
                    .addFunction(getTypedFunBuilder.build())
                    .addFunction(getOrFailFunBuilder.build())
                    .build()
            )

            controllerBuilder
        }

        val controllers = controllerInterfaces.map { ControllerType(it.build(), packages.base) }.toSet()

        return KtorControllers(controllers)
    }

    private fun buildRouteCode(operation: Operation, verb: String, path: Map.Entry<String, Path>): CodeBlock {
        val builder = CodeBlock.builder()

        val securityOption = operation.securitySupport(globalSecurity)

        val addAuth = securityOption.allowsAuthenticated && options.contains(ControllerCodeGenOptionType.AUTHENTICATION)
        if (addAuth) {
            // Using the key from the first security requirement as the auth name
            val authName = if (operation.hasSecurityRequirements()) {
                operation.securityRequirements.first().requirements.keys.first()
            } else {
                // Fall back to the global security requirements
                this.api.openApi3.securityRequirements.first().requirements.keys.first()
            }

            builder
                .addStatement(
                    "%M(\"$authName\", optional = %L) {",
                    MemberName("io.ktor.server.auth", "authenticate"),
                    securityOption == SecuritySupport.AUTHENTICATION_OPTIONAL,
                )
                .indent()
        }

        val params = operation.toIncomingParameters(packages.base, path.value.parameters, emptyList())
        val (pathParams, queryParams, headerParams, bodyParams) = params.splitByType()

        val happyPathResponse = operation.happyPathResponse(packages.base)

        val methodName = getMethodName(operation, verb, path)

        builder
            .addStatement(
                "%M(%S) {",
                MemberName("io.ktor.server.routing", verb),
                path.key,
            )
            .indent()

        if (addAuth) {
            if (securityOption == SecuritySupport.AUTHENTICATION_OPTIONAL) {
                builder
                    .addStatement(
                        "val principal = %M.%M<%T>()",
                        MemberName("io.ktor.server.application", "call"),
                        MemberName("io.ktor.server.auth", "principal", isExtension = true),
                        ClassName("io.ktor.server.auth", "Principal")
                    )
            } else {
                builder
                    .addStatement(
                        "val principal = %M.%M<%T>() ?: throw %M(%S)", // should not happen as authenticate { ... } ensures principal is present
                        MemberName("io.ktor.server.application", "call"),
                        MemberName("io.ktor.server.auth", "principal", isExtension = true),
                        ClassName("io.ktor.server.auth", "Principal"),
                        MemberName("kotlin", "IllegalStateException"),
                        "Principal not found"
                    )
            }
        }

        pathParams.forEach { param ->
            builder.addStatement(
                "val ${param.name} = %M.parameters.%M<${param.type}>(\"${param.originalName}\")",
                MemberName("io.ktor.server.application", "call"),
                MemberName("io.ktor.server.util", "getOrFail", isExtension = true),
            )
        }

        headerParams.forEach { param ->
            if (param.isRequired) {
                builder.addStatement(
                    "val ${param.name} = %M.request.headers.getOrFail(\"${param.originalName}\")",
                    MemberName("io.ktor.server.application", "call"),
                )
            } else {
                builder.addStatement(
                    "val ${param.name} = %M.request.headers[\"${param.originalName}\"]",
                    MemberName("io.ktor.server.application", "call"),
                )
            }
        }

        queryParams.forEach { param ->
            val typeName = param.type.copy(nullable = false) // not nullable because we handle that in the queryParameters.get* below
            if (param.isRequired) {
                builder.addStatement(
                    "val ${param.name} = %M.request.queryParameters.%M<$typeName>(\"${param.originalName}\")",
                    MemberName("io.ktor.server.application", "call"),
                    MemberName("io.ktor.server.util", "getOrFail", isExtension = true),
                )
            } else {
                builder.addStatement(
                    "val ${param.name} = %M.request.queryParameters.getTyped<$typeName>(\"${param.originalName}\")",
                    MemberName("io.ktor.server.application", "call"),
                )
            }
        }

        bodyParams.forEach { param ->
            builder.addStatement(
                "val ${param.name} = %M.%M<${param.type.simpleName()}>()",
                MemberName("io.ktor.server.application", "call"),
                MemberName("io.ktor.server.request", "receive"),
            )
        }

        val methodParameters =
            (listOf("call") + (listOf(headerParams, pathParams, queryParams, bodyParams).flatten().map { it.name })
                .plus(if (addAuth) "principal" else null)
                .filterNotNull())
                .joinToString(", ")

        if (happyPathResponse.simpleName() == Unit::class.simpleName) {
            // When return type is Unit we leave it up to the controller implementation to respond if needed.
            // Ktor will respond 200 OK by default.
            builder.addStatement("controller.$methodName($methodParameters)")
        } else {
            builder.addStatement("val result = controller.$methodName($methodParameters)")
            builder.addStatement(
                "%M.%M(result.status, result.message)",
                MemberName("io.ktor.server.application", "call"),
                MemberName("io.ktor.server.response", "respond"),
            )
        }

        builder
            .unindent()
            .addStatement("}")

        if (addAuth) {
            builder
                .unindent()
                .addStatement("}")
        }

        return builder.build()
    }

    override fun generateLibrary(): Collection<ControllerLibraryType> = listOf(
        buildControllerResult()
    )

    /**
     * Builds a data class to be used as a return type for controller methods
     */
    private fun buildControllerResult(): ControllerLibraryType {
        val genericT = TypeVariableName("T")

        val spec = TypeSpec.classBuilder(CONTROLLER_RESULT_CLASS_NAME)
            .addModifiers(KModifier.DATA)
            .addTypeVariable(genericT)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("status", ClassName("io.ktor.http", "HttpStatusCode"))
                    .addParameter("message", genericT) // naming aligned with Ktor's call.response(status, message)
                    .build()
            )
            .addProperty(
                PropertySpec.builder("status", ClassName("io.ktor.http", "HttpStatusCode"))
                    .initializer("status")
                    .build()
            )
            .addProperty(
                PropertySpec.builder("message", genericT)
                    .initializer("message")
                    .build()
            )
            .build()

        return ControllerLibraryType(spec, packages.base)
    }

    /**
     * Function for getting a typed query parameter
     *
     * Similar to Ktor's getOrFail but will not fail on missing parameter
     */
    private val getTypedFunBuilder = run {
        val returnType = TypeVariableName("R", Any::class)
            .copy(nullable = true, reified = true)

        FunSpec.builder("getTyped")
            .addModifiers(KModifier.INLINE, KModifier.PRIVATE)
            .receiver(ClassName("io.ktor.http", "Parameters"))
            .addParameter("name", String::class)
            .addTypeVariable(returnType)
            .returns(returnType)
            .addCode("""
                val values = getAll(name) ?: return null
                val typeInfo = %M<R>()
                return try {
                    @Suppress("UNCHECKED_CAST")
                    %M.fromValues(values, typeInfo) as R
                } catch (cause: Exception) {
                    throw %M(name, typeInfo.type.simpleName ?: typeInfo.type.toString(), cause)
                }
            """.trimIndent(),
                MemberName("io.ktor.util.reflect", "typeInfo"),
                MemberName("io.ktor.util.converters", "DefaultConversionService",),
                MemberName("io.ktor.server.plugins", "ParameterConversionException")
            )
            .addKdoc("""
                Gets parameter value associated with this name or null if the name is not present.
                Converting to type R using DefaultConversionService.
                
                Throws:
                  ParameterConversionException - when conversion from String to R fails
            """.trimIndent()
            )
    }

    /**
     * Function for getting typed header parameter
     */
    private val getOrFailFunBuilder =
        FunSpec.builder("getOrFail")
            .addModifiers(KModifier.PRIVATE)
            .receiver(ClassName("io.ktor.http", "Headers"))
            .returns(String::class)
            .addParameter("name", String::class)
            .addCode("""
                return this[name] ?: throw %M("Header " + name + " is required")
            """.trimIndent(), MemberName("io.ktor.server.plugins", "BadRequestException"))
            .addKdoc("""
                Gets first value from the list of values associated with a name.
                
                Throws:
                  BadRequestException - when the name is not present
            """.trimIndent()
            )

    private fun getMethodName(
        operation: Operation, verb: String, path: Map.Entry<String, Path>
    ) = ControllerGeneratorUtils.methodName(
        operation, verb, path.value.pathString.isSingleResource()
    )

    private val globalSecurity = this.api.openApi3.securityRequirements.securitySupport()

    data class KtorControllers(
        val controllers: Set<ControllerType>,
    ) : KotlinTypes(controllers) {
        override val files: Collection<FileSpec> = super.files.map { fileSpec ->
            fileSpec.toBuilder().build()
        }
    }
}

private fun List<IncomingParameter>.splitByType(): IncomingParametersByType {
    val requestParams = this.filterIsInstance<RequestParameter>()

    return IncomingParametersByType(
        pathParams = requestParams.filter { it.parameterLocation is PathParam },
        queryParams = requestParams.filter { it.parameterLocation is QueryParam },
        headerParams = requestParams.filter { it.parameterLocation is HeaderParam },
        bodyParams = this.filterIsInstance<BodyParameter>()
    )
}

private data class IncomingParametersByType(
    val pathParams: List<RequestParameter>,
    val queryParams: List<RequestParameter>,
    val headerParams: List<RequestParameter>,
    val bodyParams: List<BodyParameter>,
)

private fun TypeName.simpleName(): String = this.toString().split(".").last()
