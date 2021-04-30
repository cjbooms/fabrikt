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
            "properties"
        )
    private val simpleTypes = listOf(
        OasType.Text.type,
        OasType.Number.type,
        OasType.Integer.type,
        OasType.Boolean.type
    )

    private const val EXTENSIBLE_ENUM_KEY = "x-extensible-enum"

    fun Schema.isInternalEventsModel(): Boolean =
        listOf("EventResults", "BulkEntityDetails").any { name?.startsWith(it) ?: false }

    fun Schema.isPolymorphicSuperType(): Boolean = discriminator?.propertyName != null

    fun Schema.isInlinedObjectDefinition() =
        isObjectType() && !isSchemaLess() && Overlay.of(this).pathFromRoot.contains("properties")

    fun Schema.isInlinedArrayDefinition() =
        isArrayType() && !isSchemaLess() && this.itemsSchema.isInlinedObjectDefinition()

    fun Schema.isReferenceObjectDefinition() =
        isObjectType() && !isSchemaLess() && !Overlay.of(this).pathFromRoot.contains("properties")

    fun Schema.toModelClassName(enclosingClassName: String = "") = enclosingClassName + safeName().toModelClassName()

    fun Schema.toMapValueClassName() = safeName().toMapValueClassName()

    fun Schema.isSchemaLess() = isObjectType() && properties?.isEmpty() == true

    fun Schema.isInlineableMapDefinition() = hasAdditionalProperties() && properties?.isEmpty() == true

    fun Schema.isEnumDefinition(): Boolean =
        this.type == OasType.Text.type && (this.hasEnums() ||
            (MutableSettings.modelOptions().contains(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS) &&
                extensions.containsKey(EXTENSIBLE_ENUM_KEY)))

    @Suppress("UNCHECKED_CAST")
    fun Schema.getEnumValues(): List<String> = when {
        this.hasEnums() -> this.enums.map { it.toString() }
        !MutableSettings.modelOptions().contains(ModelCodeGenOptionType.X_EXTENSIBLE_ENUMS) -> emptyList()
        else -> extensions[EXTENSIBLE_ENUM_KEY]?.let { it as List<String> } ?: emptyList()
    }

    fun Schema.hasAdditionalProperties(): Boolean = isObjectType() && Overlay.of(additionalPropertiesSchema).isPresent

    fun Schema.isUnknownAdditionalProperties(oasKey: String) = type == null &&
        getSchemaNameInParent() ?: oasKey == "additionalProperties" && properties?.isEmpty() == true

    fun Schema.isUntypedAdditionalProperties(oasKey: String) = type == OasType.Object.type &&
        getSchemaNameInParent() ?: oasKey == "additionalProperties" && properties?.isEmpty() == true

    fun Schema.isTypedAdditionalProperties(oasKey: String) = type == OasType.Object.type &&
        getSchemaNameInParent() ?: oasKey == "additionalProperties" && properties?.isEmpty() != true

    fun Schema.isComplexTypedAdditionalProperties(oasKey: String) =
        getSchemaNameInParent() ?: oasKey == "additionalProperties" && properties?.isEmpty() != true && !isSimpleType()

    fun Schema.isSimpleType(): Boolean = simpleTypes.contains(type)

    fun Schema.isObjectType() = OasType.Object.type == type

    fun Schema.isArrayType() = OasType.Array.type == type

    fun Schema.isNotDefined() = type == null && !(hasAllOfSchemas() || hasOneOfSchemas() || hasAnyOfSchemas())

    fun Schema.getSchemaNameInParent(): String? = Overlay.of(this).pathInParent

    fun Schema.isPolymorphicSubType(api: OpenApi3): Boolean =
        getEnclosingSchema(api)?.let { it.allOfSchemas.any { it.discriminator.propertyName != null } } ?: false

    fun Schema.getPolymorphicSubTypes(api: OpenApi3): Collection<Schema> =
        api.schemas.values.filter { it.isPolymorphicSubType(api) && it.getSuperType(api) == this }

    fun Schema.getSuperType(api: OpenApi3): Schema? =
        getEnclosingSchema(api)?.let { it.allOfSchemas.firstOrNull { it.discriminator.propertyName != null } }

    fun Schema.getEnclosingSchema(api: OpenApi3): Schema? = api.schemas.values.firstOrNull { it.name == safeName() }

    fun Schema.isRequired(
        prop: Map.Entry<String, Schema>,
        markReadWriteOnlyOptional: Boolean,
        markAllOptional: Boolean
    ): Boolean =
        if (markAllOptional || (prop.value.isReadOnly && markReadWriteOnlyOptional) || (prop.value.isWriteOnly && markReadWriteOnlyOptional))
            false
        else requiredFields.contains(prop.key) || isDiscriminatorProperty(prop) // A discriminator property should be required

    fun Schema.isDiscriminatorProperty(prop: Map.Entry<String, Schema>): Boolean =
        discriminator?.propertyName == prop.key

    fun Schema.getKeyIfDiscriminator(
        prop: Map.Entry<String, Schema>,
        enclosingSchema: Schema
    ): PropertyInfo.DiscriminatorKey? =
        if (isDiscriminatorProperty(prop))
            discriminator.mappingKey(enclosingSchema).let {
                if (prop.value.isEnumDefinition()) PropertyInfo.DiscriminatorKey.EnumKey(it)
                else PropertyInfo.DiscriminatorKey.StringKey(it)
            }
        else null

    fun Discriminator.mappingKey(enclosingSchema: Schema): String =
        mappings?.entries?.firstOrNull {
            it.value.toString().contains(enclosingSchema.name)
        }?.key ?: enclosingSchema.name.toModelClassName()

    fun Schema.isInLinedObjectUnderAllOf(): Boolean =
        Overlay.of(this).pathFromRoot
            .splitToSequence("/")
            .toList()
            .let { path ->
                path[path.lastIndex].toIntOrNull() != null && (path[path.lastIndex - 1] == "allOf")
            }

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
            allOfSchemas?.firstOrNull { it.type != null } != null -> allOfSchemas.first { it.type != null }.type
            oneOfSchemas?.firstOrNull { it.type != null } != null -> oneOfSchemas.first { it.type != null }.type
            anyOfSchemas?.firstOrNull { it.type != null } != null -> anyOfSchemas.first { it.type != null }.type
            else -> "object"
        }

    private fun Schema.isOneOfPolymorphicTypes() =
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

    fun String.uriToClassName(): String = toResourceNames().joinToString("-").toModelClassName()

    fun String.toResourceNames(): Collection<String> = split("/")
        .filterNot { it.isBlank() || it.matches("\\{.*}".toRegex()) }

    fun String.isSingleResource(): Boolean =
        count { it == '/' } % 2 == 0 && endsWith("}")

    fun Path.isSingleResource(): Boolean =
        pathString.isSingleResource()

    fun Path.getResourceName() = pathString
        .split("/")
        .filterNot { it.isBlank() || it.matches("\\{.*}".toRegex()) }
        .last()
}
