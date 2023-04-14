package com.cjbooms.fabrikt.cli

import com.cjbooms.fabrikt.generators.JakartaAnnotations
import com.cjbooms.fabrikt.generators.JavaxValidationAnnotations
import com.cjbooms.fabrikt.generators.ValidationAnnotations

enum class CodeGenerationType(val description: String) {
    HTTP_MODELS(
        "Jackson annotated data classes to represent the schema objects defined in the input."
    ),
    CONTROLLERS(
        "Spring / Micronaut annotated HTTP controllers for each of the endpoints defined in the input."
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
    RESILIENCE4J("Generates a fault tolerance service for the client using the following library \"io.github.resilience4j:resilience4j-all:+\"");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ModelCodeGenOptionType(val description: String) {
    X_EXTENSIBLE_ENUMS("This option treats x-extensible-enums as enums"),
    JAVA_SERIALIZATION("This option adds Java Serializable interface to the generated models"),
    QUARKUS_REFLECTION("This option adds @RegisterForReflection to the generated models. Requires dependency \"'io.quarkus:quarkus-core:+\""),
    MICRONAUT_INTROSPECTION("This option adds @Introspected to the generated models. Requires dependency \"'io.micronaut:micronaut-core:+\""),
    MICRONAUT_REFLECTION("This option adds @ReflectiveAccess to the generated models. Requires dependency \"'io.micronaut:micronaut-core:+\""),
    INCLUDE_COMPANION_OBJECT("This option adds a companion object to the generated models.");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ControllerCodeGenOptionType(val description: String) {
    SUSPEND_MODIFIER("This option adds the suspend modifier to the generated controller functions"),
    AUTHENTICATION("This option adds the authentication parameter to the generated controller functions");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class ControllerCodeGenTargetType(val description: String) {
    SPRING("Generate for Spring framework."),
    MICRONAUT("Generate for Micronaut framework.");

    override fun toString() = "`${super.toString()}` - $description"
}

enum class CodeGenTypeOverride(val description: String) {
    DATETIME_AS_INSTANT("Use `Instant` as the datetime type. Defaults to `OffsetDateTime`"),
    DATETIME_AS_LOCALDATETIME("Use `LocalDateTime` as the datetime type. Defaults to `OffsetDateTime`");
    override fun toString() = "`${super.toString()}` - $description"
}

enum class ValidationLibrary(val description: String, val annotations: ValidationAnnotations) {
    JAVAX_VALIDATION("Use `javax.validation` annotations in generated model classes (default)", JavaxValidationAnnotations),
    JAKARTA_VALIDATION("Use `jakarta.validation` annotations in generated model classes", JakartaAnnotations);
    override fun toString() = "`${super.toString()}` - $description"
}