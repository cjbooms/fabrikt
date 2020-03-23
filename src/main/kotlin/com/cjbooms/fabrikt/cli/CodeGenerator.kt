package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.generators.JacksonModelGenerator
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.SourceApi

object CodeGenerator {

    fun generate(
        basePackage: String,
        sourceApi: SourceApi,
        generationTypes: Set<CodeGenerationType>,
        codeGenOptions: Set<ClientCodeGenOptionType>
    ): Collection<GeneratedFile> {
        val models = JacksonModelGenerator(basePackage, sourceApi).generate()
        return setOf(KotlinSourceSet(models.files))
    }
}
