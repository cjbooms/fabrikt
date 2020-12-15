package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.util.KaizenParserExtensions.getKeyIfDiscriminator
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getPolymorphicSubTypes
import com.cjbooms.fabrikt.util.KaizenParserExtensions.hasAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isDiscriminatorProperty
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInLinedObjectUnderAllOf
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlineableMapDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedObjectDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isRequired
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSchemaLess
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toModelClassName
import com.cjbooms.fabrikt.util.NormalisedString.camelCase
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema

sealed class PropertyInfo {
    abstract val oasKey: String
    abstract val typeInfo: KotlinTypeInfo
    open val isRequired: Boolean = false
    open val isInherited: Boolean = false

    val name: String
        get() = oasKey.camelCase()

    companion object {

        data class Settings(
            val markAsInherited: Boolean = false,
            val markReadWriteOnlyOptional: Boolean = true,
            val markAllOptional: Boolean = false,
            val excludeWriteOnly: Boolean = false
        )

        val HTTP_SETTINGS = Settings()

        fun Schema.topLevelProperties(settings: Settings, enclosingSchema: Schema? = null): Collection<PropertyInfo> {
            val results = emptyList<PropertyInfo>() +
                allOfSchemas.flatMap {
                    it.topLevelProperties(
                        maybeMarkInherited(
                            settings,
                            it
                        ),
                        this
                    )
                } +
                oneOfSchemas.flatMap { it.topLevelProperties(settings.copy(markAllOptional = true), this) } +
                anyOfSchemas.flatMap { it.topLevelProperties(settings.copy(markAllOptional = true), this) } +
                getInLinedProperties(settings, enclosingSchema)
            return results.toSet()
        }

        private fun maybeMarkInherited(settings: Settings, it: Schema) =
            settings.copy(markAsInherited = if (it.isInLinedObjectUnderAllOf()) settings.markAsInherited else true)

        private fun Schema.getInLinedProperties(
            settings: Settings,
            enclosingSchema: Schema? = null
        ): Collection<PropertyInfo> {
            val mainProperties = properties.map { property ->
                when (property.value.safeType()) {
                    OasType.Array.type ->
                        ListField(
                            isRequired(property, settings.markReadWriteOnlyOptional, settings.markAllOptional),
                            property.key,
                            property.value,
                            settings.markAsInherited,
                            this
                        )
                    OasType.Object.type ->
                        if (property.value.isInlineableMapDefinition() || property.value.isSchemaLess())
                            MapField(
                                isRequired = isRequired(
                                    property,
                                    settings.markReadWriteOnlyOptional,
                                    settings.markAllOptional
                                ),
                                oasKey = property.key,
                                schema = property.value,
                                isInherited = settings.markAsInherited,
                                parentSchema = this
                            )
                        else if (property.value.isInlinedObjectDefinition())
                            ObjectInlinedField(
                                isRequired = isRequired(property, settings.markReadWriteOnlyOptional, settings.markAllOptional),
                                oasKey = property.key,
                                schema = property.value,
                                isInherited = settings.markAsInherited,
                                parentSchema = this,
                                enclosingSchema = enclosingSchema
                            )
                        else
                            ObjectRefField(
                                isRequired(property, settings.markReadWriteOnlyOptional, settings.markAllOptional),
                                property.key,
                                property.value,
                                settings.markAsInherited,
                                this
                            )
                    else ->
                        if (property.value.isWriteOnly && settings.excludeWriteOnly) {
                            null
                        } else {
                            Field(
                                isRequired(property, settings.markReadWriteOnlyOptional, settings.markAllOptional),
                                oasKey = property.key,
                                schema = property.value,
                                isInherited = settings.markAsInherited,
                                isPolymorphicDiscriminator = isDiscriminatorProperty(property),
                                maybeDiscriminator = enclosingSchema?.let { this.getKeyIfDiscriminator(property, it) }
                            )
                        }
                }
            }.filterNotNull()
            return if (hasAdditionalProperties())
                mainProperties
                    .plus(
                        AdditionalProperties(additionalPropertiesSchema, settings.markAsInherited, this)
                    )
            else mainProperties
        }

        fun Schema.propertiesInPolymorphicHierarchy(api3: OpenApi3, settings: Settings): Collection<PropertyInfo> {
            if (!isPolymorphicSuperType()) throw IllegalArgumentException("Model is not a polymorphic super type")
            val results = emptyList<PropertyInfo>() +
                topLevelProperties(settings.copy(markAsInherited = true)) +
                getPolymorphicSubTypes(api3).flatMap { subType ->
                    emptyList<PropertyInfo>() +
                        subType.getInLinedProperties(settings) +
                        subType.allOfSchemas
                            .filterNot { it == this }
                            .flatMap { it.topLevelProperties(settings.copy(markAsInherited = !it.isInLinedObjectUnderAllOf())) } +
                        subType.oneOfSchemas.flatMap { it.topLevelProperties(settings.copy(markAllOptional = true)) } +
                        subType.anyOfSchemas.flatMap { it.topLevelProperties(settings.copy(markAllOptional = true)) }
                }
            return results.toSet()
        }
    }

    sealed class DiscriminatorKey(val stringValue: String) {
        class StringKey(value: String) : DiscriminatorKey(value)
        class EnumKey(value: String) : DiscriminatorKey(value) {
            val enumKey = value.toUpperCase()
        }
    }

    data class Field(
        override val isRequired: Boolean,
        override val oasKey: String,
        val schema: Schema,
        override val isInherited: Boolean,
        val isPolymorphicDiscriminator: Boolean,
        val maybeDiscriminator: DiscriminatorKey?
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
        val pattern: String? = schema.safeField(Schema::getPattern)
        val maxLength: Int? = schema.safeField(Schema::getMaxLength)
        val minLength: Int? = schema.safeField(Schema::getMinLength)
        val minimum: Number? = schema.safeField(Schema::getMinimum)
        val exclusiveMinimum: Boolean? = schema.safeField(Schema::getExclusiveMinimum)
        val maximum: Number? = schema.safeField(Schema::getMaximum)
        val exclusiveMaximum: Boolean? = schema.safeField(Schema::getExclusiveMaximum)

        private fun <T> Schema.safeField(getField: Schema.() -> T?): T? =
            when {
                this.getField() != null -> this.getField()
                allOfSchemas?.firstOrNull { it.getField() != null } != null -> allOfSchemas.first { it.getField() != null }.getField()
                oneOfSchemas?.firstOrNull { it.getField() != null } != null -> oneOfSchemas.first { it.getField() != null }.getField()
                anyOfSchemas?.firstOrNull { it.getField() != null } != null -> anyOfSchemas.first { it.getField() != null }.getField()
                else -> null
            }
    }

    interface CollectionValidation {
        val minItems: Int?
        val maxItems: Int?
    }

    class DummyField(
        override val oasKey: String,
        override val typeInfo: KotlinTypeInfo,
        override val isRequired: Boolean = true,
        override val minItems: Int? = null,
        override val maxItems: Int? = null
    ) : PropertyInfo(), CollectionValidation

    data class ListField(
        override val isRequired: Boolean,
        override val oasKey: String,
        val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo(), CollectionValidation {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
        override val minItems: Int? = schema.minItems
        override val maxItems: Int? = schema.maxItems
    }

    data class MapField(
        override val isRequired: Boolean,
        override val oasKey: String,
        val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
    }

    data class ObjectRefField(
        override val isRequired: Boolean,
        override val oasKey: String,
        val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
    }

    data class ObjectInlinedField(
        override val isRequired: Boolean,
        override val oasKey: String,
        val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema,
        val enclosingSchema: Schema?
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey, enclosingSchema?.toModelClassName() ?: "")
    }

    data class AdditionalProperties(
        val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val oasKey: String = "properties"
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, "additionalProperties")
        override val isRequired: Boolean = true
    }
}
