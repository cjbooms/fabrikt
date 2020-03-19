package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.SourceApi

object CodeGenerator {

    fun generate(
        basePackage: String,
        sourceApi: SourceApi,
        generationTypes: Set<CodeGenerationType>,
        codeGenOptions: Set<ClientCodeGenOptionType>
    ): Collection<GeneratedFile> {
        TODO("Implement me!")
    }
}
