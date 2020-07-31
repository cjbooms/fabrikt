package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
import com.cjbooms.fabrikt.model.Destinations.clientPackage
import com.cjbooms.fabrikt.model.Destinations.controllersPackage
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.Destinations.servicesPackage
import com.cjbooms.fabrikt.util.NormalisedString.toKotlinParameterName
import com.reprezen.kaizen.oasparser.model3.Parameter
import com.reprezen.kaizen.oasparser.model3.Schema
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

sealed class GeneratedType(val spec: TypeSpec, val destinationPackage: String) {
    val className = ClassName(destinationPackage, spec.name!!)
}

abstract class KotlinTypes(types: Collection<GeneratedType>) {
    open val files: Collection<FileSpec> = types.map {
        FileSpec.builder(types.first().destinationPackage, it.className.simpleName)
            .addType(it.spec)
            .build()
    }.toSet()
}

class ModelType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, modelsPackage(basePackage))

class ClientType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, clientPackage(basePackage)) {
    companion object {
        const val SIMPLE_CLIENT_SUFFIX = "Client"
        const val ENHANCED_CLIENT_SUFFIX = "Service"
    }
}

class ServiceType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, servicesPackage(basePackage)) {
    companion object {
        const val SUFFIX = "Service"
    }
}

class ControllerType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, controllersPackage(basePackage)) {
    companion object {
        const val SUFFIX = "Controller"
    }
}

data class Models(val models: Collection<ModelType>) : KotlinTypes(models) {
    override val files: Collection<FileSpec> = models.toFileSpec()
}

data class Clients(val clients: Collection<ClientType>) : KotlinTypes(clients) {
    override val files: Collection<FileSpec> = clients.toFileSpec()
}

data class Services(val services: Collection<ServiceType>) : KotlinTypes(services) {
    override val files: Collection<FileSpec> = services.toFileSpec()
}

data class Controllers(val controllers: Collection<ControllerType>) : KotlinTypes(controllers) {
    override val files: Collection<FileSpec> = super.files.map {
        it.toBuilder()
            .addImport(SpringImports.Static.REQUEST_METHOD.first, SpringImports.Static.REQUEST_METHOD.second)
            .addImport(
                SpringImports.Static.RESPONSE_STATUS.first,
                SpringImports.Static.RESPONSE_STATUS.second
            )
            .build()
    }
}

data class MethodSignature(val name: String, val returnType: TypeName, val parameters: List<IncomingParameter>)

fun <T : GeneratedType> Collection<T>.toFileSpec(): Collection<FileSpec> = groupClasses(this)
    .map {
        val builder = FileSpec.builder(it.key.destinationPackage, it.key.className.simpleName)
            .addType(it.key.spec)
        it.value.forEach { additional ->
            builder.addType(additional.spec)
        }
        builder.build()
    }

private fun <T : GeneratedType> groupClasses(allModels: Collection<T>): Map<T, List<T>> {
    val sealedClasses = allModels
        .filter { it.spec.modifiers.contains(KModifier.SEALED) }
        .map { sealedClass ->
            sealedClass to allModels.filter { maybeImpl -> maybeImpl.spec.superclass == sealedClass.className }
        }.toMap()
    val otherClasses = allModels
        .filterNot { modelType -> sealedClasses.keys.contains(modelType) }
        .filterNot { modelType -> sealedClasses.values.flatten().contains(modelType) }
        .map { it to emptyList<T>() }
        .toMap()
    return sealedClasses.plus(otherClasses)
}

/**
 * The IncomingParameter class is intended to represent a given name and type
 * of an incoming request, be it either a header, url param, path param, or body
 */
sealed class IncomingParameter(val oasName: String, val type: TypeName) {
    val name: String = oasName.toKotlinParameterName()
    fun toParameterSpecBuilder(): ParameterSpec.Builder =
        ParameterSpec.builder(name, type)
}

class BodyParameter(oasName: String, type: TypeName, val schema: Schema) : IncomingParameter(oasName, type)

class RequestParameter(
    oasName: String,
    type: TypeName,
    val parameterLocation: RequestParameterLocation,
    val typeInfo: KotlinTypeInfo,
    val minimum: Number? = null,
    val maximum: Number? = null,
    val isRequired: Boolean = false,
    val defaultValue: Any? = null
) : IncomingParameter(oasName, type) {
    constructor(oasName: String, type: TypeName, parameter: Parameter) : this(
        oasName = oasName,
        type = type,
        typeInfo = KotlinTypeInfo.from(parameter.schema, oasName),
        minimum = parameter.schema.minimum,
        maximum = parameter.schema.maximum,
        parameterLocation = RequestParameterLocation(parameter.`in`),
        isRequired = parameter.isRequired,
        defaultValue = parameter.schema.default
    )
}

object MethodNames {
    const val READ = "read"
    const val QUERY = "query"
    const val CREATE = "create"
    const val UPDATE = "update"
    const val REMOVE_SUBRESOURCE = "removeSubresource"
    const val ADD_SUBRESOURCE = "addSubresource"
}
