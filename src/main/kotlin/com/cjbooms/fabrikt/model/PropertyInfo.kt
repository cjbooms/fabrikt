package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.util.KaizenParserExtensions.getKeyIfSingleDiscriminatorValue
import com.cjbooms.fabrikt.util.KaizenParserExtensions.hasAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.hasNoDiscriminator
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isDiscriminatorProperty
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInLinedObjectUnderAllOf
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedArrayDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedObjectDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isRequired
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSchemaLess
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSimpleMapDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toModelClassName
import com.cjbooms.fabrikt.util.NormalisedString.camelCase
import com.cjbooms.fabrikt.util.NormalisedString.toEnumName
import com.reprezen.kaizen.oasparser.model3.Schema

sealed class PropertyInfo {
    abstract val oasKey: String
    abstract val typeInfo: KotlinTypeInfo
    abstract val schema: Schema
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
            val results = mutableListOf<PropertyInfo>() +
                allOfSchemas.flatMap {
                    it.topLevelProperties(
                        maybeMarkInherited(
                            settings,
                            enclosingSchema,
                            it
                        ),
                        this
                    )
                } +
                (if (oneOfSchemas.isEmpty()) emptyList() else listOf(OneOfAny(oneOfSchemas.first()))) +
                anyOfSchemas.flatMap { it.topLevelProperties(settings.copy(markAllOptional = true), this) } +
                getInLinedProperties(settings, enclosingSchema)
            return results.distinctBy { it.oasKey }
        }

        private fun maybeMarkInherited(settings: Settings, enclosingSchema: Schema?, it: Schema): Settings {
            val isInherited = when {
                it.safeName() == enclosingSchema?.name -> false
                it.hasNoDiscriminator() -> settings.markAsInherited
                it.isInLinedObjectUnderAllOf() && it.hasNoDiscriminator() -> settings.markAsInherited
                else -> true
            }
            return settings.copy(markAsInherited = isInherited)
        }

        private fun Schema.getInLinedProperties(
            settings: Settings,
            enclosingSchema: Schema? = null
        ): Collection<PropertyInfo> {
            val mainProperties: List<PropertyInfo> = properties.map { property ->
                when (property.value.safeType()) {
                    OasType.Array.type ->
                        ListField(
                            isRequired(property, settings.markReadWriteOnlyOptional, settings.markAllOptional),
                            property.key,
                            property.value,
                            settings.markAsInherited,
                            this,
                            if (property.value.isInlinedArrayDefinition() || property.value.itemsSchema.isInlinedEnumDefinition())
                                enclosingSchema
                            else null
                        )
                    OasType.Object.type ->
                        if (property.value.isSimpleMapDefinition() || property.value.isSchemaLess())
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
                                isRequired = isRequired(
                                    property, settings.markReadWriteOnlyOptional, settings.markAllOptional
                                ),
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
                                maybeDiscriminator = enclosingSchema?.let {
                                    this.getKeyIfSingleDiscriminatorValue(property, it)
                                },
                                enclosingSchema = if (property.value.isInlinedEnumDefinition()) this else null
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
    }

    sealed class DiscriminatorKey(val stringValue: String, val modelName: String) {
        class StringKey(value: String, modelName: String) : DiscriminatorKey(value, modelName)
        class EnumKey(value: String, modelName: String) : DiscriminatorKey(value, modelName) {
            val enumKey = value.toEnumName()
        }
    }

    data class Field(
        override val isRequired: Boolean,
        override val oasKey: String,
        override val schema: Schema,
        override val isInherited: Boolean,
        val isPolymorphicDiscriminator: Boolean,
        val maybeDiscriminator: Map<String, DiscriminatorKey>?,
        val enclosingSchema: Schema? = null
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo =
            KotlinTypeInfo.from(schema, oasKey, enclosingSchema?.toModelClassName() ?: "")
        val pattern: String? = schema.safeField(Schema::getPattern)
        val maxLength: Int? = schema.safeField(Schema::getMaxLength)
        val minLength: Int? = schema.safeField(Schema::getMinLength)
        val minimum: Number? = schema.safeField(Schema::getMinimum)
        val exclusiveMinimum: Boolean? = schema.safeField(Schema::getExclusiveMinimum)
        val maximum: Number? = schema.safeField(Schema::getMaximum)
        val exclusiveMaximum: Boolean? = schema.safeField(Schema::getExclusiveMaximum)

        private fun <T> Schema.safeField(getField: Schema.() -> T?): T? = this.getField()
    }

    interface CollectionValidation {
        val minItems: Int?
        val maxItems: Int?
    }

    data class ListField(
        override val isRequired: Boolean,
        override val oasKey: String,
        override val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema,
        val enclosingSchema: Schema?
    ) : PropertyInfo(), CollectionValidation {
        override val typeInfo: KotlinTypeInfo =
            KotlinTypeInfo.from(schema, oasKey, enclosingSchema?.toModelClassName() ?: "")
        override val minItems: Int? = schema.minItems
        override val maxItems: Int? = schema.maxItems
    }

    data class MapField(
        override val isRequired: Boolean,
        override val oasKey: String,
        override val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
    }

    data class ObjectRefField(
        override val isRequired: Boolean,
        override val oasKey: String,
        override val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, oasKey)
    }

    data class ObjectInlinedField(
        override val isRequired: Boolean,
        override val oasKey: String,
        override val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema,
        val enclosingSchema: Schema?
    ) : PropertyInfo() {
        override val typeInfo: KotlinTypeInfo =
            KotlinTypeInfo.from(schema, oasKey, enclosingSchema?.toModelClassName() ?: "")
    }

    data class AdditionalProperties(
        override val schema: Schema,
        override val isInherited: Boolean,
        val parentSchema: Schema
    ) : PropertyInfo() {
        override val oasKey: String = "properties"
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, "additionalProperties")
        override val isRequired: Boolean = true
    }

    data class OneOfAny(
        override val schema: Schema,
    ) : PropertyInfo() {
        override val oasKey: String = "oneOf"
        override val typeInfo: KotlinTypeInfo = KotlinTypeInfo.AnyType
    }
}
