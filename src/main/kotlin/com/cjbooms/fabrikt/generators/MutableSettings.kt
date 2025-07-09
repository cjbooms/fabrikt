package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.*

object MutableSettings {
    private lateinit var generationTypes: MutableSet<CodeGenerationType>
    private lateinit var controllerOptions: MutableSet<ControllerCodeGenOptionType>
    private lateinit var controllerTarget: ControllerCodeGenTargetType
    private lateinit var modelOptions: MutableSet<ModelCodeGenOptionType>
    private lateinit var modelSuffix: String
    private lateinit var clientOptions: MutableSet<ClientCodeGenOptionType>
    private lateinit var clientTarget: ClientCodeGenTargetType
    private lateinit var openfeignClientName: String
    private lateinit var typeOverrides: MutableSet<CodeGenTypeOverride>
    private lateinit var validationLibrary: ValidationLibrary
    private lateinit var externalRefResolutionMode: ExternalReferencesResolutionMode
    private lateinit var serializationLibrary: SerializationLibrary

    fun updateSettings(
        genTypes: Set<CodeGenerationType> = emptySet(),
        controllerOptions: Set<ControllerCodeGenOptionType> = emptySet(),
        controllerTarget: ControllerCodeGenTargetType = ControllerCodeGenTargetType.default,
        modelOptions: Set<ModelCodeGenOptionType> = emptySet(),
        modelSuffix: String = "",
        clientOptions: Set<ClientCodeGenOptionType> = emptySet(),
        clientTarget: ClientCodeGenTargetType = ClientCodeGenTargetType.default,
        openfeignClientName: String = ClientCodeGenOptionType.DEFAULT_OPEN_FEIGN_CLIENT_NAME,
        typeOverrides: Set<CodeGenTypeOverride> = emptySet(),
        validationLibrary: ValidationLibrary = ValidationLibrary.default,
        externalRefResolutionMode: ExternalReferencesResolutionMode = ExternalReferencesResolutionMode.default,
        serializationLibrary: SerializationLibrary = SerializationLibrary.default,
    ) {
        typeOverrides.validate()

        this.generationTypes = genTypes.toMutableSet()
        this.controllerOptions = controllerOptions.toMutableSet()
        this.controllerTarget = controllerTarget
        this.modelOptions = modelOptions.toMutableSet()
        this.modelSuffix = modelSuffix
        this.clientOptions = clientOptions.toMutableSet()
        this.clientTarget = clientTarget
        this.openfeignClientName = openfeignClientName
        this.typeOverrides = typeOverrides.toMutableSet()
        this.validationLibrary = validationLibrary
        this.externalRefResolutionMode = externalRefResolutionMode
        this.serializationLibrary = serializationLibrary
    }

    fun addOption(option: ModelCodeGenOptionType): Boolean {
        typeOverrides.validate()
        return modelOptions.add(option)
    }

    fun addOption(override: CodeGenTypeOverride): Boolean {
        (typeOverrides.toSet() + override).validate()
        return typeOverrides.add(override)
    }

    fun generationTypes() = this.generationTypes.toSet()
    fun controllerOptions() = this.controllerOptions.toSet()
    fun controllerTarget() = this.controllerTarget
    fun modelOptions() = this.modelOptions.toSet()
    fun modelSuffix() = this.modelSuffix
    fun clientOptions() = this.clientOptions.toSet()
    fun clientTarget() = this.clientTarget
    fun openfeignClientName() = this.openfeignClientName
    fun typeOverrides(): Set<CodeGenTypeOverride> {
        this.typeOverrides.validate()
        return this.typeOverrides.toSet()
    }
    fun validationLibrary() = this.validationLibrary
    fun externalRefResolutionMode() = this.externalRefResolutionMode
    fun serializationLibrary() = this.serializationLibrary

    public fun Set<CodeGenTypeOverride>.validate() {
        check(
            !(CodeGenTypeOverride.UUID_AS_STRING in this
                    && CodeGenTypeOverride.UUID_AS_KOTLIN_UUID in this)
        ) {
            """Conflicting UUID overrides. Use either UUID_AS_STRING or UUID_AS_KOTLIN_UUID but
                    |not both at the same time""".trimMargin()
        }
    }
}
