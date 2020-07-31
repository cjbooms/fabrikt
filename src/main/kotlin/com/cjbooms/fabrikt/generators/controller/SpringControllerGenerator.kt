package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.deleteWithNoContent
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.genericBody
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.getSingleResource
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.postWithLocationResponse
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.postWithResponsebody
import com.cjbooms.fabrikt.generators.controller.ControllerCodeBlocks.putWithNoContent
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.controllerName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.happyPathResponse
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.methodName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.controller.metadata.JavaXAnnotations
import com.cjbooms.fabrikt.generators.controller.metadata.SpringAnnotations
import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
import com.cjbooms.fabrikt.generators.service.SpringServiceInterfaceGenerator
import com.cjbooms.fabrikt.model.BodyParameter
import com.cjbooms.fabrikt.model.ControllerType
import com.cjbooms.fabrikt.model.Controllers
import com.cjbooms.fabrikt.model.HeaderParam
import com.cjbooms.fabrikt.model.ModelType
import com.cjbooms.fabrikt.model.PathParam
import com.cjbooms.fabrikt.model.QueryParam
import com.cjbooms.fabrikt.model.RequestParameter
import com.cjbooms.fabrikt.model.ServiceType
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.basePath
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class SpringControllerGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val services: Collection<ServiceType>,
    private val models: Collection<ModelType>
) {

    fun generate(): Controllers =
        Controllers(
            api.openApi3.routeToPaths().map { (resourceName, paths) ->
                buildController(resourceName, paths.values)
            }.toSet()
        )

    private fun buildController(resourceName: String, paths: Collection<Path>): ControllerType {
        val service = services.first { it.spec.name == "$resourceName${ServiceType.SUFFIX}" }

        val typeBuilder: TypeSpec.Builder = controllerBuilder(
            className = controllerName(resourceName),
            service = service,
            basePath = api.openApi3.basePath()
        )

        paths.flatMap { path ->
            path.operations
                .filter { it.key.toUpperCase() != "HEAD" }
                .map { op ->
                    val serviceFunction = SpringServiceInterfaceGenerator.toMethod(packages, op.key, op.value, path, models, api)
                            ?.let { method ->
                                service.spec.funSpecs.firstOrNull { it.name == method.name }
                            }
                    buildFunction(
                        op.value,
                        op.key,
                        path.pathString,
                        serviceFunction
                    )
                }
        }.forEach { typeBuilder.addFunction(it) }

        return ControllerType(
            typeBuilder.build(),
            packages.base
        )
    }

    private fun controllerBuilder(
        className: String,
        service: ServiceType,
        basePath: String
    ) =
        TypeSpec
            .classBuilder(className)
            .withConstructorParams(service)
            .addAnnotation(SpringAnnotations.CONTROLLER)
            .addAnnotation(SpringAnnotations.VALIDATED)
            .addAnnotation(SpringAnnotations.requestMappingBuilder().addMember("%S", basePath).build())

    private fun TypeSpec.Builder.withConstructorParams(service: ServiceType): TypeSpec.Builder {
        val parameters = mutableListOf(ParameterSpec.builder("service", service.className).build())
        val properties =
            mutableListOf(PropertySpec.builder("service", service.className).initializer("service").build())

        return primaryConstructor(
            FunSpec.constructorBuilder().addParameters(parameters).build()
        ).addProperties(properties)
    }

    private fun buildFunction(
        op: Operation,
        verb: String,
        pathString: String,
        serviceFunc: FunSpec?
    ): FunSpec {
        val methodName = methodName(verb, pathString.isSingleResource())
        val (_, returnType) = op.happyPathResponse(packages.base)
        val parameters = op.toIncomingParameters(packages.base)
        val happyResponseCodes = op.responses.keys.filter { it.startsWith("2") }.mapNotNull { it.toIntOrNull() }

        val functionCode: SimpleCodeBlock = if (happyResponseCodes.size == 1 && serviceFunc != null) {
            val upperCaseVerb = verb.toUpperCase()
            val happyResponseCode = happyResponseCodes.first()
            val isResponseBodyPresent = (op.responses["$happyResponseCode"]?.contentMediaTypes?.isNotEmpty()) ?: false
            when (upperCaseVerb) {
                "POST" -> when (happyResponseCode) {
                    200 -> postWithResponsebody(serviceFunc.name, parameters)
                    201 -> postWithLocationResponse(serviceFunc.name, parameters, isResponseBodyPresent)
                    else -> genericBody(serviceFunc.name, parameters, happyResponseCode)
                }
                "GET" -> getSingleResource(serviceFunc.name, parameters)
                "PUT" -> putWithNoContent(serviceFunc.name, parameters)
                "DELETE" -> deleteWithNoContent(serviceFunc.name, parameters)
                else -> genericBody(serviceFunc.name, parameters, happyResponseCode)
            }
        } else SimpleCodeBlock("""TODO("Implement me")""")

        val functionCodeBlock = functionCode.toCodeBlock()

        // Main method builder
        val funcSpec = FunSpec
            .builder(methodName)
            .addKdoc(op.toKdoc())
            .addCode(functionCodeBlock)
            .addSpringFunAnnotation(op, verb, pathString)
            .returns(SpringImports.RESPONSE_ENTITY.parameterizedBy(returnType))

        parameters
            .map {
                when (it) {
                    is BodyParameter -> it
                        .toParameterSpecBuilder()
                        .addAnnotation(SpringAnnotations.requestBodyBuilder().build())
                        .addAnnotation(JavaXAnnotations.validBuilder().build())
                        .build()
                    is RequestParameter -> it
                        .toParameterSpecBuilder()
                        .addValidationAnnotations(it)
                        .addSpringParamAnnotation(it)
                        .build()
                }
            }
            .forEach { funcSpec.addParameter(it) }

        return funcSpec.build()
    }

    private fun FunSpec.Builder.addSpringFunAnnotation(op: Operation, verb: String, path: String): FunSpec.Builder {
        val produces = op.responses
            .flatMap { it.value.contentMediaTypes.keys }
            .toTypedArray()

        val consumes = op.requestBody
            .contentMediaTypes.keys
            .toTypedArray()

        val funcAnnotation =
            SpringAnnotations
                .requestMappingBuilder()
                .addMember("value = [%S]", path)
                .addMember(
                    "produces = %L",
                    produces.joinToString(prefix = "[", postfix = "]", separator = ", ", transform = { "\"$it\"" })
                )
                .addMember("method = [RequestMethod.%L]", verb.toUpperCase())

        if (consumes.isNotEmpty()) {
            funcAnnotation.addMember(
                "consumes = %L",
                consumes.joinToString(prefix = "[", postfix = "]", separator = ", ", transform = { "\"$it\"" })
            )
        }

        this.addAnnotation(funcAnnotation.build())
        return this
    }

    private fun ParameterSpec.Builder.addValidationAnnotations(parameter: RequestParameter): ParameterSpec.Builder {
        if (parameter.minimum != null) this.addAnnotation(JavaXAnnotations.min(parameter.minimum.toInt()))
        if (parameter.maximum != null) this.addAnnotation(JavaXAnnotations.max(parameter.maximum.toInt()))
        if (parameter.typeInfo.isComplexType) this.addAnnotation(JavaXAnnotations.validBuilder().build())
        return this
    }

    private fun ParameterSpec.Builder.addSpringParamAnnotation(parameter: RequestParameter): ParameterSpec.Builder =
        when (parameter.parameterLocation) {
            QueryParam -> SpringAnnotations.requestParamBuilder()
            HeaderParam -> SpringAnnotations.requestHeaderBuilder()
            PathParam -> SpringAnnotations.requestPathVariableBuilder()
        }.let {
            it.addMember("value = %S", parameter.oasName)
            it.addMember("required = %L", parameter.isRequired)

            if (parameter.defaultValue != null)
                it.addMember("defaultValue = %S", parameter.defaultValue)

            this.addAnnotation(it.build())
        }
}
