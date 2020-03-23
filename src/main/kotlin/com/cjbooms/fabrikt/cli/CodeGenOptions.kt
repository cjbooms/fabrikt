package com.cjbooms.fabrikt.cli

enum class CodeGenerationType(val description: String, val requires: Set<InternalCodeGenGenType>) {
    HTTP_MODELS(
        "Jackson annotated data classes to represent the schema objects defined in the input.",
        setOf(InternalCodeGenGenType.MODELS)
    ),
    CLIENT(
        "Simple http rest client.",
        setOf(
            InternalCodeGenGenType.MODELS,
            InternalCodeGenGenType.CLIENT
        )
    );

    override fun toString() = "`${super.toString()}` - $description"
}

// Ordering is important here...
enum class InternalCodeGenGenType {
    MODELS,
    CLIENT
}

enum class ClientCodeGenOptionType(private val description: String) {
    RESILIENCE4J("Generates a fault tolerance service for the client using the following library \"io.github.resilience4j:resilience4j-all:1.2.0\"");

    override fun toString() = "`${super.toString()}` - $description"
}

data class CodeGenOptions(
    val clientOptions: Set<ClientCodeGenOptionType> = emptySet()
)
