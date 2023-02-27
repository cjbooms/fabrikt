package com.cjbooms.fabrikt.generators

import com.cjbooms.fabrikt.generators.ValidationAnnotations.fieldValid
import com.cjbooms.fabrikt.generators.model.JacksonMetadata
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.model.PropertyInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName

enum class ClassType {
    VANILLA_MODEL,
    SUPER_MODEL,
    SUB_MODEL
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
            property.initializer(name)
            property.addAnnotation(JacksonMetadata.ignore)
            val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, type)
            constructorParameter.defaultValue("mutableMapOf()")
            constructorBuilder.addParameter(constructorParameter.build())

            val value =
                if (typeInfo is KotlinTypeInfo.MapTypeAdditionalProperties) {
                    Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), parameterizedType)
                } else parameterizedType
            classBuilder.addFunction(
                FunSpec.builder("get")
                    .returns(Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), value))
                    .addStatement("return $name")
                    .addAnnotation(JacksonMetadata.anyGetter)
                    .build()
            )
            classBuilder.addFunction(
                FunSpec.builder("set")
                    .addParameter("name", String::class)
                    .addParameter("value", value)
                    .addStatement("$name[name] = value")
                    .addAnnotation(JacksonMetadata.anySetter)
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

                            else -> {
                                property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                            }
                        }
                    } else {
                        if (isInherited) {
                            property.addModifiers(KModifier.OVERRIDE)
                            classBuilder.addSuperclassConstructorParameter(name)
                        }
                        property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                    }
                    property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this)
                }

                ClassType.VANILLA_MODEL -> {
                    property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                    property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this)
                }
            }

            if (this !is PropertyInfo.Field ||
                !isPolymorphicDiscriminator ||
                isSubTypeDiscriminatorWithNoValue(classType)
            ) {
                property.initializer(name)
                val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, type)
                val default = getDefaultValue(this, parameterizedType)
                if (!isRequired) default?.setDefault(constructorParameter) ?: constructorParameter.defaultValue("null")
                constructorBuilder.addParameter(constructorParameter.build())
            }
        }

        classBuilder.addProperty(property.build())
    }

    private fun PropertyInfo.Field.isSubTypeDiscriminatorWithNoValue(classType: ClassType) =
        classType == ClassType.SUB_MODEL && isPolymorphicDiscriminator && maybeDiscriminator == null

    private fun getDefaultValue(propTypeInfo: PropertyInfo, parameterizedType: TypeName): OasDefault? {
        return when (propTypeInfo) {
            is PropertyInfo.Field -> propTypeInfo.schema.default?.let {
                val className = parameterizedType as? ClassName
                OasDefault.from(propTypeInfo.typeInfo, className, it)
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

            else -> {}
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
}
