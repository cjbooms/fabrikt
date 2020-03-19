package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.OasType.Companion.toOasType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getEnumValues
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isNotDefined
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toMapValueClassName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toModelClassName
import com.reprezen.kaizen.oasparser.model3.Schema
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import kotlin.reflect.KClass

sealed class KotlinTypeInfo(val modelKClass: KClass<*>, val generatedModelClassName: String? = null) {

    object Text : KotlinTypeInfo(String::class)
    object Date : KotlinTypeInfo(LocalDate::class)
    object DateTime : KotlinTypeInfo(OffsetDateTime::class)
    object Double : KotlinTypeInfo(kotlin.Double::class)
    object Float : KotlinTypeInfo(kotlin.Float::class)
    object Numeric : KotlinTypeInfo(BigDecimal::class)
    object Integer : KotlinTypeInfo(Int::class)
    object BigInt : KotlinTypeInfo(Long::class)
    object Boolean : KotlinTypeInfo(kotlin.Boolean::class)
    object UntypedObject : KotlinTypeInfo(Any::class)
    data class Object(val simpleClassName: String) : KotlinTypeInfo(GeneratedType::class, simpleClassName)
    data class Array(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(List::class)
    data class Map(val parameterizedType: KotlinTypeInfo) : KotlinTypeInfo(Map::class)
    object UnknownProperties : KotlinTypeInfo(Any::class)
    object UntypedProperties : KotlinTypeInfo(Any::class)
    data class TypedProperties(val simpleClassName: String) : KotlinTypeInfo(GeneratedType::class, simpleClassName)
    data class Enum(val entries: List<String>, val enumClassName: String) :
        KotlinTypeInfo(GeneratedType::class, enumClassName)

    val isComplexType: kotlin.Boolean
        get() = when (this) {
            is Array, is Object, is Map, is TypedProperties -> true
            else -> false
        }

    companion object {
        fun from(schema: Schema, oasKey: String = ""): KotlinTypeInfo =
            when (schema.toOasType(oasKey)) {
                OasType.Date -> Date
                OasType.DateTime -> DateTime
                OasType.Text -> Text
                OasType.Enum -> Enum(schema.getEnumValues(), schema.toModelClassName())
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
                    else Array(from(schema.itemsSchema, oasKey))
                OasType.Object -> Object(schema.toModelClassName())
                OasType.Map -> Map(from(schema.additionalPropertiesSchema, "additionalProperties"))
                OasType.TypedProperties -> TypedProperties(schema.toMapValueClassName())
                OasType.UntypedProperties -> UntypedProperties
                OasType.UntypedObject -> UntypedObject
                OasType.UnknownProperties -> UnknownProperties
            }
    }
}
