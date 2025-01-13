package lib

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.cli.ExternalReferencesResolutionMode
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary
import io.ktor.http.Parameters

data class GenerationSettings(
    val genTypes: Set<CodeGenerationType>,
    val serializationLibrary: SerializationLibrary = SerializationLibrary.default,
    val modelOptions: Set<ModelCodeGenOptionType>,
    val controllerTarget: ControllerCodeGenTargetType = ControllerCodeGenTargetType.default,
    val controllerOptions: Set<ControllerCodeGenOptionType> = emptySet(),
    val modelSuffix: String = "",
    val clientOptions: Set<ClientCodeGenOptionType> = emptySet(),
    val clientTarget: ClientCodeGenTargetType = ClientCodeGenTargetType.default,
    val typeOverrides: Set<CodeGenTypeOverride> = emptySet(),
    val validationLibrary: ValidationLibrary = ValidationLibrary.default,
    val externalRefResolutionMode: ExternalReferencesResolutionMode = ExternalReferencesResolutionMode.default,
    val inputSpec: String,
) {
    companion object {
        /**
         * Receives generation settings from the request.
         * Can be used for both query and form parameters.
         */
        fun Parameters.receiveGenerationSettings() = GenerationSettings(
            genTypes = this.getAll("genTypes")?.map { CodeGenerationType.valueOf(it) }?.toSet() ?: setOf(
                CodeGenerationType.HTTP_MODELS
            ),

            serializationLibrary = this["serializationLibrary"]?.let { SerializationLibrary.valueOf(it) }
                ?: SerializationLibrary.default,

            modelOptions = this.getAll("modelOptions")?.map { ModelCodeGenOptionType.valueOf(it) }?.toSet() ?: setOf(
                ModelCodeGenOptionType.SEALED_INTERFACES_FOR_ONE_OF
            ),

            controllerTarget = this["controllerTarget"]?.let { ControllerCodeGenTargetType.valueOf(it) }
                ?: ControllerCodeGenTargetType.default,

            controllerOptions = this.getAll("controllerOptions")?.map { ControllerCodeGenOptionType.valueOf(it) }
                ?.toSet() ?: emptySet(),

            modelSuffix = this["modelSuffix"] ?: "",

            clientOptions = this.getAll("clientOptions")?.map { ClientCodeGenOptionType.valueOf(it) }?.toSet()
                ?: emptySet(),

            clientTarget = this["clientTarget"]?.let { ClientCodeGenTargetType.valueOf(it) }
                ?: ClientCodeGenTargetType.default,

            typeOverrides = this.getAll("typeOverrides")?.map { CodeGenTypeOverride.valueOf(it) }?.toSet()
                ?: emptySet(),

            validationLibrary = this["validationLibrary"]?.let { ValidationLibrary.valueOf(it) }
                ?: ValidationLibrary.default,

            externalRefResolutionMode = this["externalRefResolutionMode"]?.let {
                ExternalReferencesResolutionMode.valueOf(
                    it
                )
            } ?: ExternalReferencesResolutionMode.default,

            inputSpec = this["spec"] ?: "")
    }

    /**
     * Construct the query parameters string for the settings
     */
    fun toQueryParams(): String {
        return emptySet<String>().asSequence()
            .plus(genTypes.map { "genTypes=${it.name}" })
            .plus(serializationLibrary.let { "serializationLibrary=${it.name}" })
            .plus(modelOptions.map { "modelOptions=${it.name}" })
            .plus(controllerTarget.let { "controllerTarget=${it.name}" })
            .plus(controllerOptions.map { "controllerOptions=${it.name}" }).plus(modelSuffix.let { "modelSuffix=$it" })
            .plus(clientOptions.map { "clientOptions=${it.name}" }).plus(clientTarget.let { "clientTarget=${it.name}" })
            .plus(typeOverrides.map { "typeOverrides=${it.name}" })
            .plus(validationLibrary.let { "validationLibrary=${it.name}" })
            .plus(externalRefResolutionMode.let { "externalRefResolutionMode=${it.name}" })
            .joinToString("&")
    }
}
