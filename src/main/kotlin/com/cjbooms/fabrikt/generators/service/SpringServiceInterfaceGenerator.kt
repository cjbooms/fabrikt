package com.cjbooms.fabrikt.generators.service

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.controller.ControllerGeneratorUtils.toIncomingParameters
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator.Companion.toModelType
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.model.MethodNames
import com.cjbooms.fabrikt.model.MethodSignature
import com.cjbooms.fabrikt.model.ModelType
import com.cjbooms.fabrikt.model.ServiceType
import com.cjbooms.fabrikt.model.Services
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSimpleType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.routeToPaths
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.OperationUtils
import com.cjbooms.fabrikt.util.OperationUtils.maybeListResponseType
import com.cjbooms.fabrikt.util.SupportedOperation
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

class SpringServiceInterfaceGenerator(
    private val packages: Packages,
    private val api: SourceApi,
    private val models: Collection<ModelType>
) {

    fun generate() = Services(
        api.openApi3.routeToPaths().map { (resourceName, paths) ->
            val typeBuilder = TypeSpec.interfaceBuilder("$resourceName${ServiceType.SUFFIX}")

            paths.values
                .flatMap { pathToMethods(it) }
                .map { toFunSpec(it) }
                .forEach { typeBuilder.addFunction(it) }

            ServiceType(typeBuilder.build(), packages.base)
        }.toSet()
    )

    fun pathToMethods(path: Path): List<MethodSignature> =
        path.operations
            .map { (verb, operation) ->
                toMethod(
                    packages.base,
                    verb,
                    operation,
                    path,
                    models,
                    api
                )
            }
            .filterNotNull()
            .distinct()

    private fun toFunSpec(method: MethodSignature): FunSpec {
        val spec = FunSpec
            .builder(method.name)
            .addModifiers(KModifier.ABSTRACT)
            .returns(method.returnType)

        method
            .parameters
            .map { it.toParameterSpecBuilder().build() }
            .forEach { spec.addParameter(it) }

        return spec.build()
    }

    companion object {
        fun toMethod(
            basePackage: String,
            verb: String,
            operation: Operation,
            path: Path,
            models: Collection<ModelType>,
            api: SourceApi
        ): MethodSignature? {
            val supportedOperation = OperationUtils.supportedOperationFrom(verb, operation, path, api)

            return toMethod(basePackage, operation, models, supportedOperation)
        }

        /**
         * This method relies heavily on a lot of fabrikt assumptions in order to
         * build a service like interface representation
         */
        private fun toMethod(
            basePackage: String,
            operation: Operation,
            models: Collection<ModelType>,
            supportedOperation: SupportedOperation?
        ): MethodSignature? = supportedOperation?.let { supportedOp ->
            when (supportedOp) {
                SupportedOperation.CREATE, SupportedOperation.CREATE_SUBRESOURCE, SupportedOperation.DEFAULT_POST -> {
                    val type =
                        if (OperationUtils.isResponseBodyPresent(operation)) responseType(operation)
                        else requestType(operation)

                    val returnType =
                        if (type != null) {
                            if (type.isSimpleType()) toModelType(basePackage, KotlinTypeInfo.from(type))
                            else models.find { it.spec.name == type.safeName() }?.className
                                ?: throw RuntimeException(
                                    "Could not find `${type.safeName()}` schema when trying to create Service Interface for `$supportedOp` " +
                                        "operation. Available schemata are: ${models.map { it.spec.name }.joinToString()}"
                                )
                        } else Unit::class.asTypeName()

                    MethodSignature(
                        name = MethodNames.CREATE,
                        returnType = returnType,
                        parameters = operation.toIncomingParameters(basePackage)
                    )
                }
                SupportedOperation.READ, SupportedOperation.READ_SUBRESOURCE, SupportedOperation.READ_TOP_LEVEL_SUBRESOURCE, SupportedOperation.DEFAULT_GET -> {
                    responseType(operation)
                        ?.let { responseType ->
                            MethodSignature(
                                name = MethodNames.READ,
                                returnType = models.first { it.spec.name == responseType.safeName() }.className,
                                parameters = operation.toIncomingParameters(basePackage)
                            )
                        }
                }
                SupportedOperation.UPDATE, SupportedOperation.UPDATE_SUBRESOURCE, SupportedOperation.DEFAULT_PUT -> {
                    val requestType = requestType(operation)
                    val returnType =
                        if (requestType != null) models.first { it.spec.name == requestType.safeName() }.className
                        else Unit::class.asTypeName()

                    MethodSignature(
                        name = MethodNames.UPDATE,
                        returnType = returnType,
                        parameters = operation.toIncomingParameters(basePackage)
                    )
                }
                SupportedOperation.QUERY, SupportedOperation.QUERY_SUBRESOURCE, SupportedOperation.QUERY_TOP_LEVEL_SUBRESOURCE -> {
                    operation.maybeListResponseType()
                        ?.let { modelName ->
                            MethodSignature(
                                name = MethodNames.QUERY,
                                returnType = queryResultOrListType(modelName, basePackage),
                                parameters = operation.toIncomingParameters(basePackage)
                            )
                        }
                }
                SupportedOperation.ADD_TOP_LEVEL_SUBRESOURCE -> {
                    MethodSignature(
                        name = MethodNames.ADD_SUBRESOURCE,
                        returnType = Unit::class.asTypeName(),
                        parameters = operation.toIncomingParameters(basePackage)
                    )
                }
                SupportedOperation.REMOVE_TOP_LEVEL_SUBRESOURCE, SupportedOperation.DEFAULT_DELETE -> {
                    MethodSignature(
                        name = MethodNames.REMOVE_SUBRESOURCE,
                        returnType = Unit::class.asTypeName(),
                        parameters = operation.toIncomingParameters(basePackage)
                    )
                }
            }
        }

        private fun queryResultOrListType(modelName: String, basePackage: String) =
            List::class.asTypeName().parameterizedBy(ClassName(modelsPackage(basePackage), modelName))

        private fun responseType(operation: Operation): Schema? = operation.responses
            .filter { it.key != "default" }
            .flatMap { resp -> resp.value.contentMediaTypes.map { it.value?.schema } }
            .asSequence()
            .filterNotNull()
            .firstOrNull()

        private fun requestType(operation: Operation): Schema? = operation.requestBody.contentMediaTypes
            .map { it.value?.schema }
            .asSequence()
            .filterNotNull()
            .firstOrNull()
    }
}
