package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.cli.ExternalReferencesResolutionMode
import com.cjbooms.fabrikt.cli.InstantLibrary
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary

object MutableSettings {
    var generationTypes: Set<CodeGenerationType> = mutableSetOf()
        private set
    var controllerOptions: Set<ControllerCodeGenOptionType> = mutableSetOf()
        private set
    var controllerTarget: ControllerCodeGenTargetType = ControllerCodeGenTargetType.default
        private set
    var modelOptions: Set<ModelCodeGenOptionType> = mutableSetOf()
        private set
    var modelSuffix: String = ""
        private set
    var clientOptions: Set<ClientCodeGenOptionType> = mutableSetOf()
        private set
    var clientTarget: ClientCodeGenTargetType = ClientCodeGenTargetType.default
        private set
    var openfeignClientName: String = ClientCodeGenOptionType.DEFAULT_OPEN_FEIGN_CLIENT_NAME
        private set
    var typeOverrides: Set<CodeGenTypeOverride> = mutableSetOf()
        private set
    var validationLibrary: ValidationLibrary = ValidationLibrary.default
        private set
    var externalRefResolutionMode: ExternalReferencesResolutionMode = ExternalReferencesResolutionMode.default
        private set
    var serializationLibrary: SerializationLibrary = SerializationLibrary.default
        private set
    var instantLibrary: InstantLibrary = InstantLibrary.default
        private set

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
        instantLibrary: InstantLibrary = InstantLibrary.default,
    ) {
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
        this.instantLibrary = instantLibrary
    }

    fun addOption(option: ModelCodeGenOptionType) {
        modelOptions += option
    }

    fun addOption(override: CodeGenTypeOverride) {
        typeOverrides += override
    }
}
