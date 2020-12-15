package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType

object MutableSettings {
    var generationTypes: MutableSet<CodeGenerationType> = mutableSetOf()
    var modelOptions: MutableSet<ModelCodeGenOptionType> = mutableSetOf()
    var clientOptions: MutableSet<ClientCodeGenOptionType> = mutableSetOf()
    fun updateSettings(
        genTypes: Set<CodeGenerationType>,
        modelOptions: Set<ModelCodeGenOptionType>,
        clientOptions: Set<ClientCodeGenOptionType>
    ) {
        this.generationTypes = genTypes.toMutableSet()
        this.modelOptions = modelOptions.toMutableSet()
        this.clientOptions = clientOptions.toMutableSet()
    }
}
