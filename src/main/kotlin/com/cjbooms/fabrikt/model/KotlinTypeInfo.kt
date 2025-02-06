package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.SerializationLibrary.KOTLINX_SERIALIZATION
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.OasType.Companion.toOasType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getEnumValues
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isUnsupportedComplexInlinedDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedTypedAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isNotDefined
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isOneOfSuperInterfaceWithDiscriminator
import com.cjbooms.fabrikt.util.ModelNameRegistry
import com.reprezen.kaizen.oasparser.model3.Schema
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

sealed class KotlinTypeInfo(val modelKClass: KClass<*>, val generatedModelClassName: String? = null) {

    object Text : KotlinTypeInfo(String::class)
    object Date : KotlinTypeInfo(LocalDate::class)
    object KotlinxLocalDate : KotlinTypeInfo(kotlinx.datetime.LocalDate::class)
    object DateTime : KotlinTypeInfo(OffsetDateTime::class)
    object Instant : KotlinTypeInfo(java.time.Instant::class)
    object KotlinxInstant : KotlinTypeInfo(kotlinx.datetime.Instant::class)
    object LocalDateTime : KotlinTypeInfo(java.time.LocalDateTime::class)
    object Double : KotlinTypeInfo(kotlin.Double::class)
    object Float : KotlinTypeInfo(kotlin.Float::class)
    object Numeric : KotlinTypeInfo(BigDecimal::class)
    object Integer : KotlinTypeInfo(Int::class)
    object BigInt : KotlinTypeInfo(Long::class)
    object Uuid : KotlinTypeInfo(UUID::class)
    object Uri : KotlinTypeInfo(URI::class)
    object ByteArray : KotlinTypeInfo(kotlin.ByteArray::class)
    object InputStream : KotlinTypeInfo(java.io.InputStream::class)
    object Boolean : KotlinTypeInfo(kotlin.Boolean::class)
    object UntypedObject : KotlinTypeInfo(Any::class)
    object AnyType : KotlinTypeInfo(Any::class)
    data class Object(val simpleClassName: String) : KotlinTypeInfo(GeneratedType::class, simpleClassName)
    data class Array(
        val parameterizedType: KotlinTypeInfo,
        val isParameterizedTypeNullable: kotlin.Boolean = false
    ) : KotlinTypeInfo(List::class)

    data class Map(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(Map::class)
    object UnknownAdditionalProperties : KotlinTypeInfo(Any::class)
    object UntypedObjectAdditionalProperties : KotlinTypeInfo(Any::class)
    data class GeneratedTypedAdditionalProperties(val simpleClassName: String) :
        KotlinTypeInfo(GeneratedType::class, simpleClassName)

    data class SimpleTypedAdditionalProperties(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(Map::class)

    data class MapTypeAdditionalProperties(val parameterizedType: KotlinTypeInfo) :
        KotlinTypeInfo(Map::class)

    data class Enum(val entries: List<String>, val enumClassName: String) :
        KotlinTypeInfo(GeneratedType::class, enumClassName)

    val isComplexType: kotlin.Boolean
        get() = when (this) {
            is Array, is Object, is Map, is GeneratedTypedAdditionalProperties -> true
            else -> false
        }

    companion object {
        private val logger = Logger.getGlobal()

        fun from(schema: Schema, oasKey: String = "", enclosingSchema: Schema? = null): KotlinTypeInfo {
            if (schema.isUnsupportedComplexInlinedDefinition()) {
                /*
                 * Defaults to Any for complex schemas inlined under the paths section.
                 * Necessary until support for generating inlined models like these is added.
                 */
                return if (schema.isEnumDefinition()) Text else AnyType
            }
            return when (schema.toOasType(oasKey)) {
                OasType.Date -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) KotlinxLocalDate
                    else Date
                }

                OasType.DateTime -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) KotlinxInstant
                    else getOverridableDateTimeType()
                }

                OasType.Text -> Text
                OasType.Enum ->
                    Enum(schema.getEnumValues(), ModelNameRegistry.getOrRegister(schema, enclosingSchema))

                OasType.Uuid -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) Text // could possibly be Kotlin native UUID once that becomes stable
                    else Uuid
                }

                OasType.Uri -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) Text // no native URI in kotlin and thus not in kotlinx.serialization either
                    else Uri
                }

                OasType.Base64String -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) Text // no native ByteArray in kotlinx.serialization
                    else ByteArray
                }
                OasType.Binary -> {
                    if (MutableSettings.serializationLibrary() == KOTLINX_SERIALIZATION) Text // no native Binary in kotlinx.serialization
                    else getOverridableByteArray()
                }
                OasType.Double -> Double
                OasType.Float -> Float
                OasType.Number -> Numeric
                OasType.Int32 -> Integer
                OasType.Int64 -> BigInt
                OasType.Integer -> Integer
                OasType.Boolean -> Boolean
                OasType.Array ->
                    if (schema.itemsSchema.isNotDefined())
                        throw IllegalArgumentException("Property ${schema.name} cannot be parsed to a Schema. Check your input")
                    else Array(from(schema.itemsSchema, oasKey, enclosingSchema), schema.itemsSchema.isNullable)

                OasType.Object -> Object(ModelNameRegistry.getOrRegister(schema, enclosingSchema))
                OasType.Map ->
                    Map(from(schema.additionalPropertiesSchema, OasType.ADDITIONAL_PROPERTIES_VALUE, enclosingSchema))

                OasType.TypedObjectAdditionalProperties -> GeneratedTypedAdditionalProperties(
                    ModelNameRegistry.getOrRegister(schema, valueSuffix = schema.isInlinedTypedAdditionalProperties())
                )

                OasType.SimpleTypedAdditionalProperties ->
                    SimpleTypedAdditionalProperties(from(schema, OasType.ADDITIONAL_PROPERTIES_VALUE))

                OasType.UntypedObjectAdditionalProperties -> UntypedObjectAdditionalProperties
                OasType.UntypedObject -> UntypedObject
                OasType.UnknownAdditionalProperties -> UnknownAdditionalProperties
                OasType.TypedMapAdditionalProperties ->
                    MapTypeAdditionalProperties(
                        from(schema.additionalPropertiesSchema, "", enclosingSchema)
                    )

                OasType.Any -> AnyType
                OasType.OneOfAny ->
                    if (schema.isOneOfSuperInterfaceWithDiscriminator()) {
                        Object(ModelNameRegistry.getOrRegister(schema, enclosingSchema))
                    } else {
                        AnyType
                    }
            }
        }

        private fun getOverridableDateTimeType(): KotlinTypeInfo {
            val typeOverrides = MutableSettings.typeOverrides()
            return when {
                CodeGenTypeOverride.DATETIME_AS_INSTANT in typeOverrides -> Instant
                CodeGenTypeOverride.DATETIME_AS_LOCALDATETIME in typeOverrides -> LocalDateTime
                else -> DateTime
            }
        }

        private fun getOverridableByteArray(): KotlinTypeInfo {
            val types: Set<CodeGenerationType> = MutableSettings.generationTypes()
            val typeOverrides = MutableSettings.typeOverrides()
            return when {
                CodeGenerationType.CLIENT in types && CodeGenTypeOverride.BYTEARRAY_AS_INPUTSTREAM in typeOverrides -> {
                    logger.log(
                        Level.WARNING,
                        """
                            Client code generation does not support streaming, yet. The override flag 
                            'BYTEARRAY_AS_INPUTSTREAM' is ignored. If generating server side code, please consider 
                            splitting the client & server in different fabrikt executions. Defaulting to `ByteArray`...
                        """.trimIndent()
                    )
                    ByteArray
                }

                CodeGenTypeOverride.BYTEARRAY_AS_INPUTSTREAM in typeOverrides -> InputStream
                else -> ByteArray
            }
        }
    }
}
