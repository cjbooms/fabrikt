package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.util.toUpperCase
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import java.net.URI
import java.util.Base64

sealed class OasDefault {

    abstract fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder

    data class StringValue(val strValue: String) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%S", strValue)
        }
    }

    data class NumericValue(val numericValue: Number) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%L", numericValue)
        }
    }

    data class UriValue(val strValue: String) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%T(\"$strValue\")", URI::class)
        }
    }
    data class ByteValue(val base64String: String) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue(CodeBlock.of("%T.getDecoder().decode(\"$base64String\")", Base64::class))
        }
    }

    data class BooleanValue(val boolValue: Boolean) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%L", boolValue)
        }
    }

    data class EnumValue(val type: TypeName, val enumValue: String) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%T.${enumValue.toUpperCase()}", type)
        }
    }

    companion object {
        fun from(typeInfo: KotlinTypeInfo, type: TypeName?, default: Any): OasDefault? {
            return when (default) {
                is String -> {
                    when (typeInfo) {
                        is KotlinTypeInfo.Enum -> {
                            if (type != null && typeInfo.entries.contains(default)) OasDefault.EnumValue(type, default)
                            else null
                        }
                        is KotlinTypeInfo.Text -> OasDefault.StringValue(default)
                        is KotlinTypeInfo.Uri -> OasDefault.UriValue(default)
                        is KotlinTypeInfo.ByteArray -> OasDefault.ByteValue(default)
                        else -> null
                    }
                }
                is Number -> OasDefault.NumericValue(default)
                is Boolean -> OasDefault.BooleanValue(default)
                else -> null
            }
        }
    }
}
