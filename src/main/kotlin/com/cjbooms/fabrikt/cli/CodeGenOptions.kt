package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.generators.JakartaAnnotations
import com.cjbooms.fabrikt.generators.JavaxValidationAnnotations
import com.cjbooms.fabrikt.generators.NoValidationAnnotations
import com.cjbooms.fabrikt.generators.ValidationAnnotations
import com.cjbooms.fabrikt.model.SerializationAnnotations
import com.cjbooms.fabrikt.model.JacksonAnnotations
import com.cjbooms.fabrikt.model.KotlinxSerializationAnnotations

enum class CodeGenerationType(val description: String) {
    HTTP_MODELS(
        "Jackson annotated data classes to represent the schema objects defined in the input."
    ),
    CONTROLLERS(
        "Spring / Micronaut / Ktor HTTP controllers for each of the endpoints defined in the input."
    ),
    CLIENT(
        "Simple http rest client."
    ),
    QUARKUS_REFLECTION_CONFIG(
        "This options generates the reflection-config.json file for quarkus integration projects"
    );

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ClientCodeGenOptionType(private val description: String) {
    RESILIENCE4J("Generates a fault tolerance service for the client using the following library \"io.github.resilience4j:resilience4j-all:+\" (only for OkHttp clients)"),
    SUSPEND_MODIFIER("This option adds the suspend modifier to the generated client functions (only for OpenFeign clients)"),
    SPRING_RESPONSE_ENTITY_WRAPPER("This option adds the Spring-ResponseEntity generic around the response to be able to get response headers and status (only for OpenFeign clients).");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ClientCodeGenTargetType(val description: String) {
    OK_HTTP("Generate OkHttp client."),
    OPEN_FEIGN("Generate OpenFeign client.");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ModelCodeGenOptionType(val description: String) {
    X_EXTENSIBLE_ENUMS("This option treats x-extensible-enums as enums"),
    JAVA_SERIALIZATION("This option adds Java Serializable interface to the generated models"),
    QUARKUS_REFLECTION("This option adds @RegisterForReflection to the generated models. Requires dependency \"'io.quarkus:quarkus-core:+\""),
    MICRONAUT_INTROSPECTION("This option adds @Introspected to the generated models. Requires dependency \"'io.micronaut:micronaut-core:+\""),
    MICRONAUT_REFLECTION("This option adds @ReflectiveAccess to the generated models. Requires dependency \"'io.micronaut:micronaut-core:+\""),
    INCLUDE_COMPANION_OBJECT("This option adds a companion object to the generated models."),
    SEALED_INTERFACES_FOR_ONE_OF("This option enables the generation of interfaces for discriminated oneOf types"),
    NON_NULL_MAP_VALUES("This option makes map values non-null. The default (since v15) and most spec compliant is make map values nullable"),
    ;

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ControllerCodeGenOptionType(val description: String) {
    SUSPEND_MODIFIER("This option adds the suspend modifier to the generated controller functions"),
    AUTHENTICATION("This option adds the authentication parameter to the generated controller functions"),
    COMPLETION_STAGE("This option makes generated controller functions have Type CompletionStage<T> (works only with Spring Controller generator)");
    override fun toString() = "`${super.toString()}` - $description"
}

enum class ControllerCodeGenTargetType(val description: String) {
    SPRING("Generate for Spring framework."),
    MICRONAUT("Generate for Micronaut framework."),
    KTOR("Generate for Ktor server.");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class CodeGenTypeOverride(val description: String) {
    DATETIME_AS_INSTANT("Use `Instant` as the datetime type. Defaults to `OffsetDateTime`"),
    DATETIME_AS_LOCALDATETIME("Use `LocalDateTime` as the datetime type. Defaults to `OffsetDateTime`"),
    BYTEARRAY_AS_INPUTSTREAM("Use `InputStream` as ByteArray type. Defaults to `ByteArray`");
    override fun toString() = "`${super.toString()}` - $description"
}

enum class ValidationLibrary(val description: String, val annotations: ValidationAnnotations) {
    JAVAX_VALIDATION("Use `javax.validation` annotations in generated model classes (default)", JavaxValidationAnnotations),
    JAKARTA_VALIDATION("Use `jakarta.validation` annotations in generated model classes", JakartaAnnotations),
    NO_VALIDATION("Use no validation annotations in generated model classes", NoValidationAnnotations);
    override fun toString() = "`${super.toString()}` - $description"
}

enum class ExternalReferencesResolutionMode(val description: String) {
    TARGETED("Generate models only for directly referenced schemas in external API files."),
    AGGRESSIVE("Referencing any schema in an external API file triggers generation of every external schema in that file.");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class SerializationLibrary(val description: String, val serializationAnnotations: SerializationAnnotations) {
    JACKSON("Use Jackson for serialization and deserialization", JacksonAnnotations),
    KOTLINX_SERIALIZATION("**!EXPERIMENTAL!** Use kotlinx.serialization for serialization and deserialization", KotlinxSerializationAnnotations);

    override fun toString() = "`${super.toString()}` - $description"
}
