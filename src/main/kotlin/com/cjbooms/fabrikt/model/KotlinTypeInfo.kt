package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.OasType.Companion.toOasType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getEnumValues
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedTypedAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isNotDefined
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toMapValueClassName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toModelClassName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.kaizen.oasparser.model3.Schema
import java.math.BigDecimal
import java.net.URI
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.reflect.KClass

sealed class KotlinTypeInfo(val modelKClass: KClass<*>, val generatedModelClassName: String? = null) {

    object Text : KotlinTypeInfo(String::class)
    object Date : KotlinTypeInfo(LocalDate::class)
    object DateTime : KotlinTypeInfo(OffsetDateTime::class)
    object Instant: KotlinTypeInfo(java.time.Instant::class)
    object LocalDateTime: KotlinTypeInfo(java.time.LocalDateTime::class)
    object Double : KotlinTypeInfo(kotlin.Double::class)
    object Float : KotlinTypeInfo(kotlin.Float::class)
    object Numeric : KotlinTypeInfo(BigDecimal::class)
    object Integer : KotlinTypeInfo(Int::class)
    object BigInt : KotlinTypeInfo(Long::class)
    object Uuid : KotlinTypeInfo(UUID::class)
    object Uri : KotlinTypeInfo(URI::class)
    object ByteArray : KotlinTypeInfo(kotlin.ByteArray::class)
    object Boolean : KotlinTypeInfo(kotlin.Boolean::class)
    object UntypedObject : KotlinTypeInfo(Any::class)
    object AnyType : KotlinTypeInfo(Any::class)
    data class Object(val simpleClassName: String) : KotlinTypeInfo(GeneratedType::class, simpleClassName)
    data class Array(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(List::class)
    data class Map(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(Map::class)
    object UnknownAdditionalProperties : KotlinTypeInfo(Any::class)
    object UntypedObjectAdditionalProperties : KotlinTypeInfo(Any::class)
    data class GeneratedTypedAdditionalProperties(val simpleClassName: String) :
        KotlinTypeInfo(GeneratedType::class, simpleClassName)

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
        fun from(schema: Schema, oasKey: String = "", enclosingName: String = ""): KotlinTypeInfo =
            when (schema.toOasType(oasKey)) {
                OasType.Date -> Date
                OasType.DateTime -> getOverridableDateTimeType()
                OasType.Text -> Text
                OasType.Enum ->
                    Enum(schema.getEnumValues(), schema.toModelClassName(enclosingName.toModelClassName()))
                OasType.Uuid -> Uuid
                OasType.Uri -> Uri
                OasType.ByteArray -> ByteArray
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
                    else Array(from(schema.itemsSchema, oasKey, enclosingName))
                OasType.Object -> Object(schema.toModelClassName(enclosingName.toModelClassName()))
                OasType.Map ->
                    Map(from(schema.additionalPropertiesSchema, "", enclosingName))
                OasType.TypedObjectAdditionalProperties -> GeneratedTypedAdditionalProperties(
                    if (schema.isInlinedTypedAdditionalProperties()) schema.toMapValueClassName()
                    else schema.toModelClassName()
                )
                OasType.UntypedObjectAdditionalProperties -> UntypedObjectAdditionalProperties
                OasType.UntypedObject -> UntypedObject
                OasType.UnknownAdditionalProperties -> UnknownAdditionalProperties
                OasType.TypedMapAdditionalProperties ->
                    MapTypeAdditionalProperties(
                        from(schema.additionalPropertiesSchema, "", enclosingName)
                    )
                OasType.Any -> AnyType
                OasType.OneOfAny -> AnyType
            }

        private fun getOverridableDateTimeType(): KotlinTypeInfo {
            val typeOverrides = MutableSettings.typeOverrides()
            return when {
                CodeGenTypeOverride.DATETIME_AS_INSTANT in typeOverrides -> Instant
                CodeGenTypeOverride.DATETIME_AS_LOCALDATETIME in typeOverrides -> LocalDateTime
                else -> DateTime
            }
        }
    }
}
