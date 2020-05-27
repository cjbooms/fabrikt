package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.cli.CodeGenerationType.CLIENT
import com.cjbooms.fabrikt.cli.CodeGenerationType.HTTP_MODELS
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.JacksonModelGenerator
import com.cjbooms.fabrikt.generators.OkHttpClientGenerator
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.SourceApi

class CodeGenerator(
    private val packages: Packages,
    private val sourceApi: SourceApi,
    private val generationTypes: Set<CodeGenerationType>,
    private val codeGenOptions: Set<ClientCodeGenOptionType>
) {

    fun generate(): Collection<GeneratedFile> = generationTypes.map(::generateCode).flatten()

    private fun generateCode(generationType: CodeGenerationType): Collection<GeneratedFile> =
        when (generationType) {
            CLIENT -> generateClient()
            HTTP_MODELS -> generateModels()
        }

    private fun generateModels(): Collection<GeneratedFile> =
            setOf(KotlinSourceSet(JacksonModelGenerator(packages.base, sourceApi).generate().files))

    private fun generateClient(): Collection<GeneratedFile> {
        val clientGenerator = OkHttpClientGenerator(packages, sourceApi)
        val models = generateModels()
        val client = clientGenerator.generate(codeGenOptions).files
        val lib = clientGenerator.generateLibrary(codeGenOptions)
        return setOf(KotlinSourceSet(client)).plus(lib).plus(models)
    }
}
