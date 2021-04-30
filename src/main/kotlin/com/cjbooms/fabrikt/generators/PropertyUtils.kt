package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.generators.ValidationAnnotations.fieldValid
import com.cjbooms.fabrikt.generators.model.JacksonAnnotations
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.model.PropertyInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

enum class ClassType {
    VANILLA_MODEL,
    SUPER_MODEL,
    SUB_MODEL
}

private sealed class OasDefault {

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

    data class BooleanValue(val boolValue: Boolean) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%L", boolValue)
        }
    }

    data class EnumValue(val type: ClassName, val enumValue: String) : OasDefault() {
        override fun setDefault(paramSpecBuilder: ParameterSpec.Builder): ParameterSpec.Builder {
            return paramSpecBuilder.defaultValue("%T.${enumValue.toUpperCase()}", type)
        }
    }
}

object PropertyUtils {

    fun PropertyInfo.addToClass(
        type: TypeName,
        parameterizedType: TypeName,
        classBuilder: TypeSpec.Builder,
        constructorBuilder: FunSpec.Builder,
        classType: ClassType = ClassType.VANILLA_MODEL
    ) {
        val property = PropertySpec.builder(name, type)

        if (this is PropertyInfo.AdditionalProperties) {
            property.initializer("mutableMapOf()")
            classBuilder.addFunction(
                FunSpec.builder("set")
                    .addParameter("name", String::class)
                    .addParameter("value", parameterizedType)
                    .addStatement("$name[name] = value")
                    .addAnnotation(JacksonAnnotations.anySetter)
                    .build()
            )
        } else {
            when (classType) {
                ClassType.SUPER_MODEL -> {
                    if (this is PropertyInfo.Field && isPolymorphicDiscriminator) property.addModifiers(KModifier.ABSTRACT)
                    else property.addModifiers(KModifier.OPEN)
                }
                ClassType.SUB_MODEL -> {
                    if (this is PropertyInfo.Field && isPolymorphicDiscriminator) {
                        property.addModifiers(KModifier.OVERRIDE)
                        when (maybeDiscriminator) {
                            is PropertyInfo.DiscriminatorKey.EnumKey ->
                                property.initializer("%T.%L", type, maybeDiscriminator.enumKey)
                            is PropertyInfo.DiscriminatorKey.StringKey ->
                                property.initializer("%S", maybeDiscriminator.stringValue)
                        }
                    } else {
                        if (isInherited) {
                            property.addModifiers(KModifier.OVERRIDE)
                            classBuilder.addSuperclassConstructorParameter(name)
                        }
                        property.addAnnotation(JacksonAnnotations.jacksonParameterAnnotation(oasKey))
                    }
                    property.addAnnotation(JacksonAnnotations.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this)
                }
                ClassType.VANILLA_MODEL -> {
                    property.addAnnotation(JacksonAnnotations.jacksonParameterAnnotation(oasKey))
                    property.addAnnotation(JacksonAnnotations.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this)
                }
            }

            if (this !is PropertyInfo.Field || !isPolymorphicDiscriminator) {
                property.initializer(name)
                val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, type)
                val default = getDefaultValue(this, parameterizedType)
                if (!isRequired) default?.setDefault(constructorParameter) ?: constructorParameter.defaultValue("null")
                constructorBuilder.addParameter(constructorParameter.build())
            }
        }

        classBuilder.addProperty(property.build())
    }

    private fun getDefaultValue(propTypeInfo: PropertyInfo, parameterizedType: TypeName): OasDefault? {
        return when (propTypeInfo) {
            is PropertyInfo.Field -> propTypeInfo.schema.default?.let {
                val className = parameterizedType as? ClassName
                getDefaultValue(propTypeInfo, className, it)
            }
            else -> null
        }
    }

    fun PropertyInfo.isNullable() = when (this) {
        is PropertyInfo.Field -> !isRequired && schema.default == null
        else -> !isRequired
    }

    /**
     * Current Open API v3 Spec validation keys:
     *
     *   multipleOf             - Not Supported. No equivalent javax validation. We could add our own as a
     *   maximum                - Supported
     *   minimum                - Supported
     *   exclusiveMaximum       - Support - Used to mark the above as exclusive checks
     *   exclusiveMinimum       - Support - Used to mark the above as exclusive checks
     *   maxLength              - Supported
     *   minLength              - Supported
     *   pattern                - Supported
     *   maxItems               - Supported
     *   minItems               - Supported
     *   uniqueItems            - Not Supported. No equivalent javax validation. We could add our own as a
     *   maxProperties          - Not Supported. No equivalent javax validation. We could add our own as a
     *   minProperties          - Not Supported. No equivalent javax validation. We could add our own as a
     *   enum                   - Not currently supported. Possible to do as a regex maybe.
     */
    private fun PropertySpec.Builder.addValidationAnnotations(info: PropertyInfo) {
        if (!info.isNullable()) addAnnotation(ValidationAnnotations.NON_NULL_ANNOTATION)
        when (info) {
            is PropertyInfo.Field -> {
                // Regex validation pattern to validate string input
                info.pattern?.let { addAnnotation(ValidationAnnotations.regexPattern(it)) }

                // Size Restrictions for Strings
                val (min, max) = Pair(info.minLength, info.maxLength)
                if (min != null || max != null) addAnnotation(
                    ValidationAnnotations.lengthRestriction(min, max)
                )

                // Numeric value validation
                info.minimum?.let {
                    addAnnotation(
                        ValidationAnnotations.minRestriction(it, info.exclusiveMinimum ?: false)
                    )
                }
                info.maximum?.let {
                    addAnnotation(
                        ValidationAnnotations.maxRestriction(it, info.exclusiveMaximum ?: false)
                    )
                }
            }
            is PropertyInfo.CollectionValidation -> {
                // Size Restrictions for collections
                val (minCollLen, maxCollLen) = Pair(info.minItems, info.maxItems)
                if (minCollLen != null || maxCollLen != null) addAnnotation(
                    ValidationAnnotations.lengthRestriction(
                        minCollLen,
                        maxCollLen
                    )
                )
            }
        }

        when (val typeInfo = info.typeInfo) {
            is KotlinTypeInfo.Map -> {
                if (typeInfo.parameterizedType.isComplexType) {
                    addAnnotation(fieldValid())
                }
            }
            is KotlinTypeInfo.Array -> {
                if (typeInfo.parameterizedType.isComplexType) {
                    addAnnotation(fieldValid())
                }
            }
            else -> {
                if (typeInfo.isComplexType) {
                    addAnnotation(fieldValid())
                }
            }
        }
    }

    private fun getDefaultValue(field: PropertyInfo.Field, type: ClassName?, default: Any): OasDefault? {
        return when (default) {
            is String -> {
                when (val typeInfo = field.typeInfo) {
                    is KotlinTypeInfo.Enum -> {
                        if (type != null && typeInfo.entries.contains(default)) OasDefault.EnumValue(type, default)
                        else null
                    }
                    else -> OasDefault.StringValue(default)
                }
            }
            is Number -> OasDefault.NumericValue(default)
            is Boolean -> OasDefault.BooleanValue(default)
            else -> null
        }
    }
}
