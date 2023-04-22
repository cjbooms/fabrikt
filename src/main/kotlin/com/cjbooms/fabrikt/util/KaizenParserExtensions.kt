package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.OasType
import com.cjbooms.fabrikt.model.PropertyInfo
import com.cjbooms.fabrikt.util.NormalisedString.toMapValueClassName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.jsonoverlay.Overlay
import com.reprezen.kaizen.oasparser.model3.Discriminator
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Path
import com.reprezen.kaizen.oasparser.model3.Schema
import java.net.URI

object KaizenParserExtensions {

    private val invalidNames =
        listOf(
            "anyOf",
            "oneOf",
            "allOf",
            "items",
            "schema",
            "application~1json",
            "content",
            "additionalProperties",
            "properties",
        )
    private val simpleTypes = listOf(
        OasType.Text.type,
        OasType.Number.type,
        OasType.Integer.type,
        OasType.Boolean.type,
    )

    private const val EXTENSIBLE_ENUM_KEY = "x-extensible-enum"

    fun Schema.isPolymorphicSuperType(): Boolean = discriminator?.propertyName != null ||
        getDiscriminatorForInLinedObjectUnderAllOf()?.propertyName != null

    fun Schema.isInlinedObjectDefinition() =
        isObjectType() && !isSchemaLess() && Overlay.of(this).pathFromRoot.contains("properties")

    fun Schema.isInlinedTypedAdditionalProperties() =
        isObjectType() && !isSchemaLess() && Overlay.of(this).pathFromRoot.contains("additionalProperties")

    fun Schema.isInlinedEnumDefinition() =
        isEnumDefinition() && !isSchemaLess() && Overlay.of(this).pathFromRoot.contains("properties")

    fun Schema.isInlinedArrayDefinition() =
        isArrayType() && !isSchemaLess() && this.itemsSchema.isInlinedObjectDefinition()

    fun Schema.toModelClassName(enclosingClassName: String = "") = enclosingClassName + safeName().toModelClassName()

    fun Schema.toMapValueClassName() = safeName().toMapValueClassName()

    fun Schema.isSchemaLess() = isObjectType() && properties?.isEmpty() == true

    fun Schema.isSimpleMapDefinition() = hasAdditionalProperties() && properties?.isEmpty() == true

    fun Schema.isSimpleOneOfAnyDefinition() = oneOfSchemas?.isNotEmpty() == true &&
        !isOneOfPolymorphicTypes() &&
        anyOfSchemas?.isEmpty() == true &&
        allOfSchemas?.isEmpty() == true &&
        properties?.isEmpty() == true

    fun Schema.isEnumDefinition(): Boolean = this.type == OasType.Text.type && (
        this.hasEnums() || (
            MutableSettings.modelOptions().contains(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS) &&
                extensions.containsKey(EXTENSIBLE_ENUM_KEY)
            )
        )

    fun Schema.isStringDefinitionWithFormat(format: String): Boolean =
        this.type == OasType.Text.type && (this.format?.equals(format, ignoreCase = true) == true)

    @Suppress("UNCHECKED_CAST")
    fun Schema.getEnumValues(): List<String> = when {
        this.hasEnums() -> this.enums.map { it.toString() }.filterNot { it.isBlank() }
        !MutableSettings.modelOptions().contains(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS) -> emptyList()
        else -> extensions[EXTENSIBLE_ENUM_KEY]?.let { it as List<String?> }?.filterNotNull()
            ?.filterNot { it.isBlank() } ?: emptyList()
    }

    fun Schema.hasAdditionalProperties(): Boolean = isObjectType() && Overlay.of(additionalPropertiesSchema).isPresent

    fun Schema.isUnknownAdditionalProperties(oasKey: String) = type == null &&
        (getSchemaNameInParent() ?: oasKey) == "additionalProperties" && properties?.isEmpty() == true

    fun Schema.isUntypedAdditionalProperties(oasKey: String) = type == OasType.Object.type &&
        (getSchemaNameInParent() ?: oasKey) == "additionalProperties" && properties?.isEmpty() == true

    fun Schema.isTypedAdditionalProperties(oasKey: String) = type == OasType.Object.type &&
        (getSchemaNameInParent() == "additionalProperties" || oasKey == "additionalProperties") && properties?.isEmpty() != true

    fun Schema.isMapTypeAdditionalProperties(oasKey: String) = type == OasType.Object.type &&
        (oasKey == "additionalProperties") && properties?.isEmpty() == true &&
        hasAdditionalProperties()

    fun Schema.isComplexTypedAdditionalProperties(oasKey: String) = (getSchemaNameInParent() ?: oasKey) ==
        "additionalProperties" && properties?.isEmpty() != true && !isSimpleType()

    fun Schema.isSimpleType(): Boolean =
        (simpleTypes.contains(type) && !isEnumDefinition()) || isSimpleMapDefinition() || isSimpleOneOfAnyDefinition()

    private fun Schema.isObjectType() = OasType.Object.type == type

    private fun Schema.isArrayType() = OasType.Array.type == type

    fun Schema.isNotDefined() = !Overlay.of(this).isPresent &&
        type == null && !(hasAllOfSchemas() || hasOneOfSchemas() || hasAnyOfSchemas())

    private fun Schema.getSchemaNameInParent(): String? = Overlay.of(this).pathInParent

    fun Schema.isPolymorphicSubType(api: OpenApi3): Boolean =
        getEnclosingSchema(api)?.let { schema ->
            schema.allOfSchemas.any { it.isPolymorphicSuperType() }
        } ?: false

    fun Schema.getSuperType(api: OpenApi3): Schema? =
        getEnclosingSchema(api)?.let { schema ->
            schema.allOfSchemas.firstOrNull { it.isPolymorphicSuperType() }
        }

    fun Schema.getDiscriminatorForInLinedObjectUnderAllOf(): Discriminator? =
        this.allOfSchemas.firstOrNull { it.isInLinedObjectUnderAllOf() }?.discriminator

    private fun Schema.getEnclosingSchema(api: OpenApi3): Schema? =
        api.schemas.values.firstOrNull { it.name == safeName() }

    fun Schema.isRequired(
        prop: Map.Entry<String, Schema>,
        markReadWriteOnlyOptional: Boolean,
        markAllOptional: Boolean,
    ): Boolean =
        if (markAllOptional || (prop.value.isReadOnly && markReadWriteOnlyOptional) || (prop.value.isWriteOnly && markReadWriteOnlyOptional)) {
            false
        } else {
            requiredFields.contains(prop.key) || isDiscriminatorProperty(prop) // A discriminator property should be required
        }

    fun Schema.isDiscriminatorProperty(prop: Map.Entry<String, Schema>): Boolean =
        discriminator?.propertyName == prop.key

    fun Schema.getKeyIfSingleDiscriminatorValue(
        prop: Map.Entry<String, Schema>,
        enclosingSchema: Schema,
    ): Map<String, PropertyInfo.DiscriminatorKey>? =
        if (isDiscriminatorProperty(prop) && discriminator.mappingKeys(enclosingSchema).isNotEmpty()) {
            discriminator.mappingKeys(enclosingSchema).map {
                if (prop.value.isEnumDefinition()) {
                    it.key to PropertyInfo.DiscriminatorKey.EnumKey(it.key, it.value)
                } else {
                    it.key to PropertyInfo.DiscriminatorKey.StringKey(it.key, it.value)
                }
            }.toMap()
        } else {
            null
        }

    fun Discriminator.mappingKeys(enclosingSchema: Schema): Map<String, String> {
        val discriminatorMappings = mappings?.map { it.key to it.value.split("/").last() }?.toMap()
        return if (discriminatorMappings.isNullOrEmpty()) {
            mapOf(enclosingSchema.name to enclosingSchema.safeName().toModelClassName())
        } else {
            discriminatorMappings
        }
    }

    fun Schema.isInLinedObjectUnderAllOf(): Boolean =
        Overlay.of(this).pathFromRoot
            .splitToSequence("/")
            .toList()
            .let { path ->
                path[path.lastIndex].toIntOrNull() != null && (path[path.lastIndex - 1] == "allOf")
            }

    fun Schema.hasNoDiscriminator(): Boolean = this.discriminator.propertyName == null

    fun Schema.safeName(): String =
        when {
            isOneOfPolymorphicTypes() -> this.oneOfSchemas.first().allOfSchemas.first().safeName()
            name != null -> name
            else -> Overlay.of(this).pathFromRoot
                .splitToSequence("/")
                .filterNot { invalidNames.contains(it) }
                .filter { it.toIntOrNull() == null } // Ignore numeric-identifiers path-parts in: allOf / oneOf / anyOf
                .last()
        }

    fun Schema.safeType(): String? =
        when {
            type != null -> type
            properties?.isNotEmpty() == true -> "object"
            allOfSchemas.hasAnyDefinedProperties() -> "object"
            allOfSchemas?.firstOrNull { it.type != null } != null -> allOfSchemas.first { it.type != null }.type
            oneOfSchemas.hasAnyDefinedProperties() -> "object"
            oneOfSchemas?.firstOrNull { it.type != null } != null -> oneOfSchemas.first { it.type != null }.type
            anyOfSchemas.hasAnyDefinedProperties() -> "object"
            anyOfSchemas?.firstOrNull { it.type != null } != null -> anyOfSchemas.first { it.type != null }.type
            isOneOfPolymorphicTypes() -> "object"
            isUnknownAdditionalProperties("") -> "object"
            else -> null
        }

    private fun List<Schema>?.hasAnyDefinedProperties(): Boolean =
        this?.any { it.properties?.isNotEmpty() == true } == true

    fun Schema.isOneOfPolymorphicTypes() =
        this.oneOfSchemas?.firstOrNull()?.allOfSchemas?.firstOrNull() != null

    fun OpenApi3.basePath(): String =
        servers
            .firstOrNull()
            ?.url?.let { URI.create(it) }
            ?.path.orEmpty()
            .removeSuffix("/")

    /**
     * Returns a Map of String to list of openapi Path objects,
     * where the String is the uri, but in pascal case suitable
     * for naming classes for controllers and services.
     */
    fun OpenApi3.routeToPaths(): Map<String, Map<String, Path>> = paths
        .map { (name, path) -> name to path }
        .groupBy { it.first.uriToClassName() }
        .mapValues { it.value.toMap() }

    private fun String.uriToClassName(): String = toResourceNames().joinToString("-").toModelClassName()

    private fun String.toResourceNames(): Collection<String> = split("/")
        .filterNot { it.isBlank() || it.matches("\\{.*}".toRegex()) }

    fun String.isSingleResource(): Boolean =
        count { it == '/' } % 2 == 0 && endsWith("}")
}
