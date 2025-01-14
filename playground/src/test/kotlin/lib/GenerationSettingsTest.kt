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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GenerationSettingsTest {

    @Test
    fun toQueryParams() {
        val settings = GenerationSettings(
            genTypes = setOf(CodeGenerationType.HTTP_MODELS),
            serializationLibrary = SerializationLibrary.KOTLINX_SERIALIZATION,
            modelOptions = setOf(ModelCodeGenOptionType.SEALED_INTERFACES_FOR_ONE_OF),
            controllerTarget = ControllerCodeGenTargetType.KTOR,
            controllerOptions = setOf(ControllerCodeGenOptionType.AUTHENTICATION),
            modelSuffix = "Model",
            clientOptions = setOf(ClientCodeGenOptionType.RESILIENCE4J),
            clientTarget = ClientCodeGenTargetType.OK_HTTP,
            typeOverrides = setOf(CodeGenTypeOverride.DATETIME_AS_INSTANT),
            validationLibrary = ValidationLibrary.JAVAX_VALIDATION,
            externalRefResolutionMode = ExternalReferencesResolutionMode.TARGETED,
            inputSpec = "spec"
        )

        val queryParams = settings.toQueryParams()

        assertEquals("""
            genTypes=HTTP_MODELS
            &serializationLibrary=KOTLINX_SERIALIZATION
            &modelOptions=SEALED_INTERFACES_FOR_ONE_OF
            &controllerTarget=KTOR
            &controllerOptions=AUTHENTICATION
            &modelSuffix=Model
            &clientOptions=RESILIENCE4J
            &clientTarget=OK_HTTP
            &typeOverrides=DATETIME_AS_INSTANT
            &validationLibrary=JAVAX_VALIDATION
            &externalRefResolutionMode=TARGETED
        """.trimIndent().replace("\n",""), queryParams)
    }
}