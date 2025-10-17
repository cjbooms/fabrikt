package lib

import com.cjbooms.fabrikt.cli.CodeGenerator
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.Destinations
import com.cjbooms.fabrikt.model.SourceApi

fun generateCodeSynchronized(
    generationSettings: GenerationSettings
) = synchronized(MutableSettings) { // necessary because of the mutable static state
    MutableSettings.updateSettings(
        genTypes = generationSettings.genTypes,
        controllerOptions = generationSettings.controllerOptions,
        controllerTarget = generationSettings.controllerTarget,
        modelOptions = generationSettings.modelOptions,
        modelSuffix = generationSettings.modelSuffix,
        clientOptions = generationSettings.clientOptions,
        clientTarget = generationSettings.clientTarget,
        typeOverrides = generationSettings.typeOverrides,
        validationLibrary = generationSettings.validationLibrary,
        externalRefResolutionMode = generationSettings.externalRefResolutionMode,
        serializationLibrary = generationSettings.serializationLibrary,
        outputOptions = generationSettings.outputOptions
    )

    val packages = Packages("com.example")
    val sourceApi = SourceApi.create(generationSettings.inputSpec, emptyList())
    val generator = CodeGenerator(packages, sourceApi, Destinations.MAIN_KT_SOURCE, Destinations.MAIN_RESOURCES)

    generator.generate().distinct()
}
