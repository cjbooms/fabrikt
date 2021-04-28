package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.toKdoc
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.controllerName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.happyPathResponse
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.methodName
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.controller.metadata.JavaXAnnotations
import com.cjbooms.fabrikt.generators.controller.metadata.SpringAnnotations
import com.cjbooms.fabrikt.model.BodyParameter
import com.cjbooms.fabrikt.model.ControllerType
import com.cjbooms.fabrikt.model.Controllers
import com.cjbooms.fabrikt.model.HeaderParam
import com.cjbooms.fabrikt.model.ModelType
import com.cjbooms.fabrikt.model.PathParam
import com.cjbooms.fabrikt.model.QueryParam
import com.cjbooms.fabrikt.model.RequestParameter
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.basePath
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSingleResource
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec

class SpringControllerInterfaceGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val models: Collection<ModelType>
) {

    fun generate(): Controllers =
        Controllers(
            api.openApi3.routeToPaths().map { (resourceName, paths) ->
                buildController(resourceName, paths.values)
            }.toSet()
        )

    private fun buildController(resourceName: String, paths: Collection<Path>): ControllerType {
        val typeBuilder: TypeSpec.Builder = controllerBuilder(
            className = controllerName(resourceName),
            basePath = api.openApi3.basePath()
        )

        paths.flatMap { path ->
            path.operations
                .filter { it.key.toUpperCase() != "HEAD" }
                .map { op ->
                    buildFunction(
                        op.value,
                        op.key,
                        path.pathString
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
        basePath: String
    ) =
        TypeSpec.interfaceBuilder(className)
            .addAnnotation(SpringAnnotations.CONTROLLER)
            .addAnnotation(SpringAnnotations.VALIDATED)
            .addAnnotation(SpringAnnotations.requestMappingBuilder().addMember("%S", basePath).build())

    private fun buildFunction(
        op: Operation,
        verb: String,
        pathString: String
    ): FunSpec {
        val methodName = methodName(verb, pathString.isSingleResource())
        val returnType = op.happyPathResponse(packages.base)
        val parameters = op.toIncomingParameters(packages.base)

        // Main method builder
        val funcSpec = FunSpec
            .builder(methodName)
            .addModifiers(KModifier.ABSTRACT)
            .addKdoc(op.toKdoc())
            .addSpringFunAnnotation(op, verb, pathString)
            .returns(returnType)

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
