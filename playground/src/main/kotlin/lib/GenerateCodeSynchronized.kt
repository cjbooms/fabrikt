package lib

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.CodeGenerator
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.cli.ExternalReferencesResolutionMode
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.Destinations
import com.cjbooms.fabrikt.model.SourceApi

fun generateCodeSynchronized(
    genTypes: Set<CodeGenerationType>,
    serializationLibrary: SerializationLibrary?,
    modelOptions: Set<ModelCodeGenOptionType>,
    controllerTarget: ControllerCodeGenTargetType?,
    inputSpec: String,
    controllerOptions: Set<ControllerCodeGenOptionType> = emptySet(),
    modelSuffix: String?,
    clientOptions: Set<ClientCodeGenOptionType> = emptySet(),
    clientTarget: ClientCodeGenTargetType?,
    typeOverrides: Set<CodeGenTypeOverride> = emptySet(),
    validationLibrary: ValidationLibrary?,
    externalRefResolutionMode: ExternalReferencesResolutionMode?
) = synchronized(MutableSettings) { // necessary because of the mutable static state
    MutableSettings.updateSettings(
        genTypes = genTypes,
        controllerOptions = controllerOptions,
        controllerTarget = controllerTarget ?: ControllerCodeGenTargetType.SPRING, // TODO: Find out where to define defaults
        modelOptions = modelOptions,
        modelSuffix = modelSuffix ?: "",
        clientOptions = clientOptions,
        clientTarget = clientTarget ?: ClientCodeGenTargetType.OK_HTTP,
        typeOverrides = typeOverrides,
        validationLibrary = validationLibrary ?: ValidationLibrary.JAVAX_VALIDATION,
        externalRefResolutionMode = externalRefResolutionMode ?: ExternalReferencesResolutionMode.TARGETED,
        serializationLibrary = serializationLibrary ?: SerializationLibrary.JACKSON
    )

    val packages = Packages("com.example")
    val sourceApi = SourceApi.create(inputSpec, emptyList())
    val generator = CodeGenerator(packages, sourceApi, Destinations.MAIN_KT_SOURCE, Destinations.MAIN_RESOURCES)

    generator.generate().distinct()
}