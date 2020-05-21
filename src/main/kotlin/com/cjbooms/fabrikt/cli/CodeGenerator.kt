package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.cli.CodeGenerationType.CLIENT
import com.cjbooms.fabrikt.cli.CodeGenerationType.HTTP_MODELS
import com.cjbooms.fabrikt.configurations.PackagesConfig
import com.cjbooms.fabrikt.generators.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.OkHttpClientGenerator
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.SourceApi

class CodeGenerator(
    private val basePackage: String,
    private val sourceApi: SourceApi,
    private val generationTypes: Set<CodeGenerationType>,
    private val codeGenOptions: Set<ClientCodeGenOptionType>
) {
    private val packagesConfig = PackagesConfig.build(basePackage)

    fun generate(): Collection<GeneratedFile> = generationTypes.map(::generateCode).flatten()

    private fun generateCode(generationType: CodeGenerationType): Collection<GeneratedFile> =
        when (generationType) {
            CLIENT -> generateClient()
            HTTP_MODELS -> generateModels()
        }

    private fun generateModels(): Collection<GeneratedFile> =
            setOf(KotlinSourceSet(JacksonModelGenerator(basePackage, sourceApi).generate().files))

    private fun generateClient(): Collection<GeneratedFile> {
        val clientGenerator = OkHttpClientGenerator(packagesConfig, sourceApi)
        val models = generateModels()
        val client = clientGenerator.generate(codeGenOptions).files
        val lib = clientGenerator.generateLibrary(codeGenOptions)
        return setOf(KotlinSourceSet(client)).plus(lib).plus(models)
    }
}
