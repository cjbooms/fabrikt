package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.*

object MutableSettings {
    private lateinit var generationTypes: MutableSet<CodeGenerationType>
    private lateinit var controllerOptions: MutableSet<ControllerCodeGenOptionType>
    private lateinit var controllerTarget: ControllerCodeGenTargetType
    private lateinit var controllerAnnotations: MutableList<String>
    private lateinit var modelOptions: MutableSet<ModelCodeGenOptionType>
    private lateinit var modelSuffix: String
    private lateinit var modelAnnotations: MutableList<String>
    private lateinit var clientOptions: MutableSet<ClientCodeGenOptionType>
    private lateinit var clientTarget: ClientCodeGenTargetType
    private lateinit var clientAnnotations: MutableList<String>
    private lateinit var openfeignClientName: String
    private lateinit var typeOverrides: MutableSet<CodeGenTypeOverride>
    private lateinit var validationLibrary: ValidationLibrary
    private lateinit var externalRefResolutionMode: ExternalReferencesResolutionMode
    private lateinit var serializationLibrary: SerializationLibrary

    fun updateSettings(
        genTypes: Set<CodeGenerationType> = emptySet(),
        controllerOptions: Set<ControllerCodeGenOptionType> = emptySet(),
        controllerTarget: ControllerCodeGenTargetType = ControllerCodeGenTargetType.default,
        controllerAnnotations: List<String> = emptyList(),
        modelOptions: Set<ModelCodeGenOptionType> = emptySet(),
        modelSuffix: String = "",
        modelAnnotations: List<String> = emptyList(),
        clientOptions: Set<ClientCodeGenOptionType> = emptySet(),
        clientTarget: ClientCodeGenTargetType = ClientCodeGenTargetType.default,
        clientAnnotations: List<String> = emptyList(),
        openfeignClientName: String = ClientCodeGenOptionType.DEFAULT_OPEN_FEIGN_CLIENT_NAME,
        typeOverrides: Set<CodeGenTypeOverride> = emptySet(),
        validationLibrary: ValidationLibrary = ValidationLibrary.default,
        externalRefResolutionMode: ExternalReferencesResolutionMode = ExternalReferencesResolutionMode.default,
        serializationLibrary: SerializationLibrary = SerializationLibrary.default,
    ) {
        this.generationTypes = genTypes.toMutableSet()
        this.controllerOptions = controllerOptions.toMutableSet()
        this.controllerTarget = controllerTarget
        this.controllerAnnotations = controllerAnnotations.toMutableList()
        this.modelOptions = modelOptions.toMutableSet()
        this.modelSuffix = modelSuffix
        this.modelAnnotations = modelAnnotations.toMutableList()
        this.clientOptions = clientOptions.toMutableSet()
        this.clientTarget = clientTarget
        this.clientAnnotations = clientAnnotations.toMutableList()
        this.openfeignClientName = openfeignClientName
        this.typeOverrides = typeOverrides.toMutableSet()
        this.validationLibrary = validationLibrary
        this.externalRefResolutionMode = externalRefResolutionMode
        this.serializationLibrary = serializationLibrary
    }

    fun addOption(option: ModelCodeGenOptionType) = modelOptions.add(option)
    fun addOption(override: CodeGenTypeOverride) = typeOverrides.add(override)

    fun generationTypes() = this.generationTypes.toSet()
    fun controllerOptions() = this.controllerOptions.toSet()
    fun controllerTarget() = this.controllerTarget
    fun controllerAnnotations() = this.controllerAnnotations.toList()
    fun modelOptions() = this.modelOptions.toSet()
    fun modelSuffix() = this.modelSuffix
    fun modelAnnotations() = this.modelAnnotations.toList()
    fun clientOptions() = this.clientOptions.toSet()
    fun clientTarget() = this.clientTarget
    fun clientAnnotations() = this.clientAnnotations.toList()
    fun openfeignClientName() = this.openfeignClientName
    fun typeOverrides() = this.typeOverrides.toSet()
    fun validationLibrary() = this.validationLibrary
    fun externalRefResolutionMode() = this.externalRefResolutionMode
    fun serializationLibrary() = this.serializationLibrary
}
