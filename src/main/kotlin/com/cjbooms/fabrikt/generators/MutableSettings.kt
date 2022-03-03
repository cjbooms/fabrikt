package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType

object MutableSettings {
    private lateinit var generationTypes: MutableSet<CodeGenerationType>
    private lateinit var controllerOptions: MutableSet<ControllerCodeGenOptionType>
    private lateinit var modelOptions: MutableSet<ModelCodeGenOptionType>
    private lateinit var clientOptions: MutableSet<ClientCodeGenOptionType>

    fun updateSettings(
        genTypes: Set<CodeGenerationType>,
        controllerOptions: Set<ControllerCodeGenOptionType>,
        modelOptions: Set<ModelCodeGenOptionType>,
        clientOptions: Set<ClientCodeGenOptionType>
    ) {
        this.generationTypes = genTypes.toMutableSet()
        this.controllerOptions = controllerOptions.toMutableSet()
        this.modelOptions = modelOptions.toMutableSet()
        this.clientOptions = clientOptions.toMutableSet()
    }

    fun addOption(option: ModelCodeGenOptionType) = modelOptions.add(option)

    fun controllerOptions() = this.controllerOptions.toSet()
    fun modelOptions() = this.modelOptions.toSet()
    fun generationTypes() = this.generationTypes.toSet()
    fun clientOptions() = this.clientOptions.toSet()
}
