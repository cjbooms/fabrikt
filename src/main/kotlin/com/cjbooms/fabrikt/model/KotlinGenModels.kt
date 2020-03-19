package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.Destinations.clientPackage
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.Destinations.servicesPackage
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
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

class ServiceType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, servicesPackage(basePackage)) {
    companion object {
        const val SUFFIX = "Service"
    }
}

class Services(val services: Collection<ServiceType>) : KotlinTypes(services)

class ModelType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, modelsPackage(basePackage))

class ClientType(spec: TypeSpec, basePackage: String) : GeneratedType(spec, clientPackage(basePackage)) {
    companion object {
        const val SIMPLE_CLIENT_SUFFIX = "Client"
        const val ENHANCED_CLIENT_SUFFIX = "Service"
    }
}

data class Models(val models: Collection<ModelType>) : KotlinTypes(models) {
    override val files: Collection<FileSpec> = models.toFileSpec()
}

data class Clients(val clients: Collection<ClientType>) : KotlinTypes(clients) {
    override val files: Collection<FileSpec> = clients.toFileSpec()
}

private fun <T : GeneratedType> Collection<T>.toFileSpec(): Collection<FileSpec> = groupClasses(this)
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
