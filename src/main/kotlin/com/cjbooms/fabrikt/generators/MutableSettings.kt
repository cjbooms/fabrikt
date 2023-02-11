package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.*

object MutableSettings {
    private lateinit var generationTypes: MutableSet<CodeGenerationType>
    private lateinit var controllerOptions: MutableSet<ControllerCodeGenOptionType>
    private lateinit var controllerTarget: ControllerCodeGenTargetType
    private lateinit var modelOptions: MutableSet<ModelCodeGenOptionType>
    private lateinit var clientOptions: MutableSet<ClientCodeGenOptionType>

    fun updateSettings(
        genTypes: Set<CodeGenerationType> = emptySet(),
        controllerOptions: Set<ControllerCodeGenOptionType> = emptySet(),
        controllerTarget: ControllerCodeGenTargetType = ControllerCodeGenTargetType.SPRING,
        modelOptions: Set<ModelCodeGenOptionType> = emptySet(),
        clientOptions: Set<ClientCodeGenOptionType> = emptySet()
    ) {
        this.generationTypes = genTypes.toMutableSet()
        this.controllerOptions = controllerOptions.toMutableSet()
        this.controllerTarget = controllerTarget
        this.modelOptions = modelOptions.toMutableSet()
        this.clientOptions = clientOptions.toMutableSet()
    }

    fun addOption(option: ModelCodeGenOptionType) = modelOptions.add(option)

    fun generationTypes() = this.generationTypes.toSet()
    fun controllerOptions() = this.controllerOptions.toSet()
    fun controllerTarget() = this.controllerTarget
    fun modelOptions() = this.modelOptions.toSet()
    fun clientOptions() = this.clientOptions.toSet()
}
