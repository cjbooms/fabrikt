package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.cli.CodeGenerationType.CLIENT
import com.cjbooms.fabrikt.cli.CodeGenerationType.CONTROLLERS
import com.cjbooms.fabrikt.cli.CodeGenerationType.HTTP_MODELS
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.client.OkHttpClientGenerator
import com.cjbooms.fabrikt.generators.controller.SpringControllerGenerator
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.model.QuarkusReflectionModelGenerator
import com.cjbooms.fabrikt.generators.service.SpringServiceInterfaceGenerator
import com.cjbooms.fabrikt.model.Clients
import com.cjbooms.fabrikt.model.Controllers
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.ResourceFile
import com.cjbooms.fabrikt.model.ResourceSourceSet
import com.cjbooms.fabrikt.model.Services
import com.cjbooms.fabrikt.model.SourceApi
import com.squareup.kotlinpoet.FileSpec

class CodeGenerator(
    private val packages: Packages,
    private val sourceApi: SourceApi,
    private val generationTypes: Set<CodeGenerationType>,
    private val modelOptions: Set<ModelCodeGenOptionType>,
    private val codeGenOptions: Set<ClientCodeGenOptionType>
) {

    fun generate(): Collection<GeneratedFile> = generationTypes.map(::generateCode).flatten()

    private fun generateCode(generationType: CodeGenerationType): Collection<GeneratedFile> =
        when (generationType) {
            CLIENT -> generateClient()
            CONTROLLERS -> generateControllers()
            HTTP_MODELS -> generateModels()
        }

    private fun generateModels(): Collection<GeneratedFile> {
        val models = models()
        val resources = resources(models)
        return sourceSet(models.files).plus(resourceSet(resources))
    }

    private fun generateServiceInterfaces(): Collection<GeneratedFile> =
        sourceSet(services().files).plus(sourceSet(models().files))

    private fun generateControllers(): Collection<GeneratedFile> =
        sourceSet(controllers().files).plus(generateServiceInterfaces())

    private fun generateClient(): Collection<GeneratedFile> {
        val lib = OkHttpClientGenerator(packages, sourceApi).generateLibrary(codeGenOptions)
        return sourceSet(client().files).plus(lib).plus(sourceSet(models().files))
    }

    private fun sourceSet(fileSpec: Collection<FileSpec>) = setOf(KotlinSourceSet(fileSpec))

    private fun resourceSet(fileSpec: Collection<ResourceFile>) = setOf(ResourceSourceSet(fileSpec))

    private fun models(): Models =
        JacksonModelGenerator(packages, sourceApi, modelOptions).generate()

    private fun resources(models: Models): List<ResourceFile> =
        listOfNotNull(QuarkusReflectionModelGenerator(models, modelOptions).generate())

    private fun services(): Services =
        SpringServiceInterfaceGenerator(packages, sourceApi, models().models).generate()

    private fun controllers(): Controllers =
        SpringControllerGenerator(packages, sourceApi, services().services, models().models).generate()

    private fun client(): Clients =
        OkHttpClientGenerator(packages, sourceApi).generate(codeGenOptions)
}
