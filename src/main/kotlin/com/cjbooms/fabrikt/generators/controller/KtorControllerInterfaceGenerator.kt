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
import com.cjbooms.fabrikt.model.Import
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
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
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

                        controllerFunBuilder.returns(
                            ClassName(packages.controllers, CONTROLLER_RESULT_CLASS_NAME)
                                .parameterizedBy(operation.happyPathResponse(packages.base))
                        )

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

                        controllerFunBuilder.addKdoc(operation.toKdoc(params))

                        controllerBuilder.addFunction(controllerFunBuilder.build())
                    }
            }

            // add the companion object with the route functions
            controllerBuilder.addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(routeFunBuilder.build())
                    .build()
            )

            controllerBuilder
        }

        // imports for the generated code
        val globalImports = listOf( // imports necessary for ktor
                "io.ktor.server.application" to "call",
                "io.ktor.server.application" to "ApplicationCall",
                "io.ktor.server.auth" to "authenticate",
                "io.ktor.server.plugins" to "BadRequestException",
                "io.ktor.server.request" to "receive",
                "io.ktor.server.response" to "respond",
                "io.ktor.server.util" to "getOrFail",
                "${packages.controllers}.RoutingUtils" to "getTyped", // utility for parsing query parameters
                "${packages.controllers}.RoutingUtils" to "getOrFail", // utility for parsing header parameters
            )
            .plus(usedVerbs.map { "io.ktor.server.routing" to it })
            .map { Import(it.first, it.second) }
            .toSet()

        val controllers = controllerInterfaces.map { ControllerType(it.build(), packages.base) }.toSet()

        return KtorControllers(controllers, globalImports)
    }

    private fun buildRouteCode(operation: Operation, verb: String, path: Map.Entry<String, Path>): String {
        val codeBlock = StringBuilder()

        val globalSecurity = this.api.openApi3.securityRequirements.securitySupport()
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

            if (securityOption == SecuritySupport.AUTHENTICATION_OPTIONAL) {
                codeBlock.appendLine("authenticate(\"$authName\", optional = true) {")
            } else {
                codeBlock.appendLine("authenticate(\"$authName\") {")
            }
        }

        val params = operation.toIncomingParameters(packages.base, path.value.parameters, emptyList())
        val (pathParams, queryParams, headerParams, bodyParams) = params.splitByType()

        val happyPathResponse = operation.happyPathResponse(packages.base)

        val methodName = getMethodName(operation, verb, path)

        codeBlock.appendLine("${verb}(\"${path.key}\") {")

        pathParams.forEach { param ->
            codeBlock.appendLine("val ${param.name} = call.parameters.getOrFail<${param.type}>(\"${param.originalName}\")")
        }

        headerParams.forEach { param ->
            if (param.isRequired) {
                codeBlock.appendLine("val ${param.name} = call.request.headers.getOrFail(\"${param.originalName}\")")
            } else {
                codeBlock.appendLine("val ${param.name} = call.request.headers[\"${param.originalName}\"]")
            }
        }

        queryParams.forEach { param ->
            val typeName = param.type.copy(nullable = false) // not nullable because we handle that in the queryParameters.get* below
            if (param.isRequired) {
                codeBlock.appendLine("val ${param.name} = call.request.queryParameters.getOrFail<$typeName>(\"${param.originalName}\")")
            } else {
                codeBlock.appendLine("val ${param.name} = call.request.queryParameters.getTyped<$typeName>(\"${param.originalName}\")")
            }
        }

        bodyParams.forEach { param ->
            codeBlock.appendLine("val ${param.name} = call.receive<${param.type.simpleName()}>()")
        }

        val methodParameters =
            listOf(headerParams, pathParams, queryParams, bodyParams).flatten()
                .joinToString(", ") { it.name }
                .let {
                    if (it.isNotEmpty()) "call, $it"
                    else "call"
                }

        codeBlock.appendLine("val result = controller.$methodName($methodParameters)")

        if (happyPathResponse.simpleName() == Unit::class.simpleName) {
            // When return type is Unit we only respond with the status code.
            // Note however that in some cases the controller may choose to respond directly on the call
            // in which case that takes precedence in Ktor's routing. For example: call.respondRedirect().
            codeBlock.appendLine("call.respond(result.status)")
        } else {
            codeBlock.appendLine("call.respond(result.status, result.message)")
        }

        codeBlock.appendLine("}")

        if (addAuth) {
            codeBlock.appendLine("}")
        }

        return codeBlock.toString()
    }

    override fun generateLibrary(): Collection<ControllerLibraryType> = listOf(
        buildRoutingUtils(),
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
     * Builds a utility object to be used for parsing query parameters
     */
    private fun buildRoutingUtils(): ControllerLibraryType {
        val genericR = TypeVariableName("R", Any::class)

        val returnType = genericR.copy(nullable = true, reified = true)

        val spec = TypeSpec.objectBuilder("RoutingUtils")
            .addFunction(
                // Function for getting a typed query parameter
                // Similar to Ktor's getOrFail but will not fail on missing parameter
                FunSpec.builder("getTyped")
                    .addModifiers(KModifier.INLINE)
                    .receiver(ClassName("io.ktor.http", "Parameters"))
                    .addParameter("name", String::class)
                    .addTypeVariable(returnType)
                    .returns(returnType)
                    .addCode("""
                        val values = getAll(name) ?: return null
                        val typeInfo = typeInfo<R>()
                        return try {
                            @Suppress("UNCHECKED_CAST")
                            DefaultConversionService.fromValues(values, typeInfo) as R
                        } catch (cause: Exception) {
                            throw ParameterConversionException(name, typeInfo.type.simpleName ?: typeInfo.type.toString(), cause)
                        }
                    """.trimIndent())
                    .addKdoc("""
                        Gets parameter value associated with this name or null if the name is not present.
                        Converting to type R using DefaultConversionService.
                        
                        Throws:
                          ParameterConversionException - when conversion from String to R fails
                    """.trimIndent())
                    .build()
            )
            .addFunction(
                // Function for getting typed header parameter
                FunSpec.builder("getOrFail")
                    .receiver(ClassName("io.ktor.http", "Headers"))
                    .returns(String::class)
                    .addParameter("name", String::class)
                    .addCode("""
                        return this[name] ?: throw BadRequestException("Header " + name + " is required")
                    """.trimIndent())
                    .addKdoc("""
                        Gets first value from the list of values associated with a name.
                        
                        Throws:
                          BadRequestException - when the name is not present
                    """.trimIndent())
                    .build()
            )
            .build()

        return ControllerLibraryType(spec, packages.base, imports = setOf(
            Import("io.ktor.util.reflect", "typeInfo"),
            Import("io.ktor.server.plugins", "BadRequestException"),
            Import("io.ktor.util.converters", "DefaultConversionService"),
            Import("io.ktor.server.plugins", "ParameterConversionException"),
        ))
    }

    private fun getMethodName(
        operation: Operation, verb: String, path: Map.Entry<String, Path>
    ) = ControllerGeneratorUtils.methodName(
        operation, verb, path.value.pathString.isSingleResource()
    )

    private val usedVerbs: List<String> =
        api.openApi3.routeToPaths()
            .flatMap { resourceToPaths -> resourceToPaths.value.flatMap { it.value.operations.keys } }
            .distinct()

    data class KtorControllers(
        val controllers: Set<ControllerType>,
        val globalImports: Set<Import>,
    ) : KotlinTypes(controllers) {
        override val files: Collection<FileSpec> = super.files.map { fileSpec ->
            fileSpec.toBuilder().apply {
                // Could be improved by only importing what is necessary in the specific file
                globalImports.forEach { addImport(it.packageName, it.name) }
            }.build()
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
