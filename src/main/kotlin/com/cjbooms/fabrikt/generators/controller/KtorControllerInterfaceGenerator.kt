package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKCodeName
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

                        // generate controller interface function
                        val controllerFun = buildControllerFun(operation, verb, path)

                        controllerBuilder.addFunction(controllerFun)
                    }
            }

            // add the companion object with the route functions
            controllerBuilder.addType(
                TypeSpec.companionObjectBuilder()
                    .addFunction(routeFunBuilder.build())
                    .addFunction(getTypedFun)
                    .addFunction(getOrFailFun)
                    .build()
            )

            controllerBuilder.build()
        }

        return KtorControllers(
            controllerInterfaces.map { ControllerType(it, packages.base) }.toSet()
        )
    }

    /**
     * Builds the base controller function that takes in all parameters.
     */
    private fun buildControllerFun(operation: Operation, verb: String, path: Map.Entry<String, Path>): FunSpec {
        val methodName = getMethodName(operation, verb, path)
        val builder = FunSpec.builder(methodName)
            .addModifiers(setOf(KModifier.SUSPEND, KModifier.ABSTRACT))

        val params = operation.toIncomingParameters(packages.base, path.value.parameters, emptyList())
        val (pathParams, queryParams, headerParams, bodyParams) = params.splitByType()

        headerParams.forEach { param ->
            // TODO: Consider handling header parameter types. Currently everything is a string.
            if (param.isRequired) {
                builder.addParameter(ParameterSpec.builder(param.name, String::class).build())
            } else {
                builder.addParameter(
                    ParameterSpec.builder(param.name, String::class.asTypeName().copy(nullable = true)).build()
                )
            }
        }

        (pathParams + queryParams).forEach { param ->
            if (param.isRequired) {
                builder.addParameter(param.toParameterSpecBuilder().build())
            } else {
                builder.addParameter(
                    ParameterSpec.builder(param.name, param.type.copy(nullable = true)).build()
                )
            }
        }

        bodyParams.forEach { param ->
            builder.addParameter(param.toParameterSpecBuilder().build())
        }

        val securityOption = operation.securitySupport(globalSecurity)
        val addAuth = securityOption.allowsAuthenticated && options.contains(ControllerCodeGenOptionType.AUTHENTICATION)
        if (addAuth) {
            builder.addParameter(
                "principal",
                ClassName(
                    "io.ktor.server.auth",
                    "Principal"
                ).copy(nullable = securityOption == SecuritySupport.AUTHENTICATION_OPTIONAL)
            ).build()
        }

        builder.addKdoc(buildControllerFunKdoc(operation, params))

        builder.addParameter("call", ClassName("io.ktor.server.application", "ApplicationCall"))

        return builder.build()
    }

    /**
     * Builds the code that goes into the route function.
     */
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
                    "val ${param.name} = %M.request.queryParameters.%M<$typeName>(\"${param.originalName}\")",
                    MemberName("io.ktor.server.application", "call"),
                    MemberName(packages.controllers, "getTyped"),
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
            listOf(headerParams, pathParams, queryParams, bodyParams).asSequence().flatten().map { it.name }
                .plus(if (addAuth) "principal" else null)
                .filterNotNull()
                .plus("call")
                .joinToString(", ")

        builder.addStatement("controller.$methodName($methodParameters)")

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

    private fun buildControllerFunKdoc(operation: Operation, parameters: List<IncomingParameter>): CodeBlock {
        val kDoc = CodeBlock.builder()

        // add summary and description
        val methodDesc = listOf(operation.summary.orEmpty(), operation.description.orEmpty()).filter { it.isNotEmpty() }
        if (methodDesc.isNotEmpty()) {
            methodDesc.forEach { kDoc.add("%L\n", it) }
            kDoc.add("\n")
        }

        // document the response
        val happyPathResponse = operation.happyPathResponse(packages.base)
        if (happyPathResponse.isUnit()) {
            kDoc.add("Route is expected to respond with status ${operation.responses.keys.first()}.\n")
        } else if (operation.responses.isNotEmpty()) {
            kDoc.add(
                "Route is expected to respond with [%L].\n",
                happyPathResponse.toString(),
            )
        }

        // document how to send response
        kDoc.add(
            "Use [%M] to send the response.\n\n",
            MemberName("io.ktor.server.response", "respond", isExtension = true)
        )

        // document parameters
        parameters.forEach {
            kDoc.add("@param %L %L\n", it.name.toKCodeName(), it.description?.trimIndent().orEmpty()).build()
        }
        kDoc.add("@param call The Ktor application call\n")

        return kDoc.build()
    }

    override fun generateLibrary(): Collection<ControllerLibraryType> = emptySet()

    /**
     * Function for getting a typed query parameter
     *
     * Similar to Ktor's getOrFail but will not fail on missing parameter
     */
    private val getTypedFun = run {
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
            .build()
    }

    /**
     * Function for getting typed header parameter
     */
    private val getOrFailFun =
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
            .build()

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

private fun TypeName.isUnit(): Boolean = this == Unit::class.asTypeName()
private fun TypeName.isNotUnit(): Boolean = !isUnit()
