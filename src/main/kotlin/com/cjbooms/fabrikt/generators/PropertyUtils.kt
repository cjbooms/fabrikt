package com.cjbooms.fabrikt.generators

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

data class ClassSettings(
    val polymorphyType: PolymorphyType,
    val extensions: Map<String, Any> = emptyMap(),
) {

    val isMergePatchPattern = extensions["x-json-merge-patch"] as? Boolean ?: false
    val addJsonIncludeNonNullAnnotation = extensions["x-jackson-include-non-null"] as? Boolean ?: false

    enum class PolymorphyType {
        NONE,
        SUPER,
        SUB,
        ONE_OF,
    }
}

object PropertyUtils {
    fun PropertyInfo.addToClass(
        schemaName: String,
        type: TypeName,
        parameterizedType: TypeName,
        classBuilder: TypeSpec.Builder,
        constructorBuilder: FunSpec.Builder,
        classSettings: ClassSettings = ClassSettings(ClassSettings.PolymorphyType.NONE),
        validationAnnotations: ValidationAnnotations = JavaxValidationAnnotations,
    ) {
        val wrappedType =
            if (classSettings.isMergePatchPattern && !this.isRequired) {
                ClassName(
                    "org.openapitools.jackson.nullable",
                    "JsonNullable",
                ).parameterizedBy(type.copy(nullable = this.schema.isNullable))
            } else {
                type
            }
        val property = PropertySpec.builder(name, wrappedType)

        if (this is PropertyInfo.AdditionalProperties) {
            property.initializer(name)
            property.addAnnotation(JacksonMetadata.ignore)
            val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, wrappedType)
            constructorParameter.defaultValue("mutableMapOf()")
            constructorBuilder.addParameter(constructorParameter.build())

            val value =
                if (typeInfo is KotlinTypeInfo.MapTypeAdditionalProperties) {
                    Map::class.asTypeName()
                        .parameterizedBy(String::class.asTypeName(), parameterizedType.copy(nullable = true))
                } else {
                    parameterizedType
                }.copy(nullable = true)
            classBuilder.addFunction(
                FunSpec.builder("get")
                    .returns(Map::class.asTypeName().parameterizedBy(String::class.asTypeName(), value))
                    .addStatement("return $name")
                    .addAnnotation(JacksonMetadata.anyGetter)
                    .build(),
            )
            classBuilder.addFunction(
                FunSpec.builder("set")
                    .addParameter("name", String::class)
                    .addParameter("value", value)
                    .addStatement("$name[name] = value")
                    .addAnnotation(JacksonMetadata.anySetter)
                    .build(),
            )
        } else {
            when (classSettings.polymorphyType) {
                ClassSettings.PolymorphyType.SUPER -> {
                    if (this is PropertyInfo.Field && isPolymorphicDiscriminator) {
                        property.addModifiers(KModifier.ABSTRACT)
                    } else {
                        property.addModifiers(KModifier.OPEN)
                    }
                }

                ClassSettings.PolymorphyType.SUB -> {
                    if (this is PropertyInfo.Field && isPolymorphicDiscriminator) {
                        property.addModifiers(KModifier.OVERRIDE)
                    } else {
                        if (isInherited) {
                            property.addModifiers(KModifier.OVERRIDE)
                            classBuilder.addSuperclassConstructorParameter(name)
                        }
                        property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                    }
                    property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this, validationAnnotations)
                }

                ClassSettings.PolymorphyType.NONE -> {
                    property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                    property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this, validationAnnotations)
                }

                ClassSettings.PolymorphyType.ONE_OF -> {
                    property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
                    property.addValidationAnnotations(this, validationAnnotations)
                }
            }

            if (isDiscriminatorFieldWithSingleKnownValue(classSettings, schemaName)) {
                this as PropertyInfo.Field
                if (classSettings.polymorphyType in listOf(ClassSettings.PolymorphyType.SUB, ClassSettings.PolymorphyType.ONE_OF)) {
                    property.initializer(name)
                    property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
                    val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, wrappedType)
                    val discriminators = maybeDiscriminator.getDiscriminatorMappings(schemaName)
                    when (val discriminator = discriminators.first()) {
                        is PropertyInfo.DiscriminatorKey.EnumKey ->
                            constructorParameter.defaultValue("%T.%L", wrappedType, discriminator.enumKey)

                        is PropertyInfo.DiscriminatorKey.StringKey ->
                            constructorParameter.defaultValue("%S", discriminator.stringValue)
                    }
                    constructorBuilder.addParameter(constructorParameter.build())
                }
            } else {
                property.initializer(name)
                val constructorParameter: ParameterSpec.Builder = ParameterSpec.builder(name, wrappedType)
                val oasDefault = getDefaultValue(this, parameterizedType)
                if (!isRequired) {
                    if (classSettings.addJsonIncludeNonNullAnnotation) {
                        property.addAnnotation(JacksonMetadata.JSON_INCLUDE)
                    }
                    if (oasDefault != null) {
                        val wrappedDefault =
                            if (classSettings.isMergePatchPattern) {
                                OasDefault.JsonNullableValue(oasDefault)
                            } else {
                                oasDefault
                            }
                        constructorParameter.defaultValue(wrappedDefault.getDefault())
                    } else {
                        val undefinedDefault = if (classSettings.isMergePatchPattern) {
                            "JsonNullable.undefined()"
                        } else {
                            "null"
                        }
                        constructorParameter.defaultValue(undefinedDefault)
                    }
                }
                constructorBuilder.addParameter(constructorParameter.build())
            }
        }

        classBuilder.addProperty(property.build())
    }

    private fun PropertyInfo.isDiscriminatorFieldWithSingleKnownValue(
        classSettings: ClassSettings,
        schemaName: String,
    ) = this is PropertyInfo.Field &&
        isPolymorphicDiscriminator &&
        !isSubTypeDiscriminatorWithNoValue(classSettings) &&
        !isSubTypeDiscriminatorWithMultipleValues(classSettings, schemaName)

    private fun Map<String, PropertyInfo.DiscriminatorKey>?.getDiscriminatorMappings(
        schemaName: String,
    ): List<PropertyInfo.DiscriminatorKey> =
        this?.filter {
            it.value.stringValue == schemaName || it.value.modelName == schemaName
        }?.map { it.value }.orEmpty()

    private fun PropertyInfo.Field.isSubTypeDiscriminatorWithNoValue(classType: ClassSettings) =
        classType.polymorphyType == ClassSettings.PolymorphyType.SUB &&
            isPolymorphicDiscriminator &&
            maybeDiscriminator == null

    private fun PropertyInfo.Field.isSubTypeDiscriminatorWithMultipleValues(
        classType: ClassSettings,
        schemaName: String,
    ) =
        classType.polymorphyType == ClassSettings.PolymorphyType.SUB &&
            isPolymorphicDiscriminator &&
            maybeDiscriminator.getDiscriminatorMappings(schemaName).size > 1

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
        is PropertyInfo.Field -> !isRequired && schema.default == null || schema.isNullable
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
    private fun PropertySpec.Builder.addValidationAnnotations(
        info: PropertyInfo,
        validationAnnotations: ValidationAnnotations,
    ) {
        if (!info.isNullable()) addAnnotation(validationAnnotations.nonNullAnnotation)
        when (info) {
            is PropertyInfo.Field -> {
                // Regex validation pattern to validate string input
                info.pattern?.let { addAnnotation(validationAnnotations.regexPattern(it)) }

                // Size Restrictions for Strings
                val (min, max) = Pair(info.minLength, info.maxLength)
                if (min != null || max != null) {
                    addAnnotation(
                        validationAnnotations.lengthRestriction(min, max),
                    )
                }

                // Numeric value validation
                info.minimum?.let {
                    addAnnotation(
                        validationAnnotations.minRestriction(it, info.exclusiveMinimum ?: false),
                    )
                }
                info.maximum?.let {
                    addAnnotation(
                        validationAnnotations.maxRestriction(it, info.exclusiveMaximum ?: false),
                    )
                }
            }

            is PropertyInfo.CollectionValidation -> {
                // Size Restrictions for collections
                val (minCollLen, maxCollLen) = Pair(info.minItems, info.maxItems)
                if (minCollLen != null || maxCollLen != null) {
                    addAnnotation(
                        validationAnnotations.lengthRestriction(
                            minCollLen,
                            maxCollLen,
                        ),
                    )
                }
            }

            else -> {}
        }

        when (val typeInfo = info.typeInfo) {
            is KotlinTypeInfo.Map -> {
                if (typeInfo.parameterizedType.isComplexType) {
                    addAnnotation(validationAnnotations.fieldValid())
                }
            }

            is KotlinTypeInfo.Array -> {
                if (typeInfo.parameterizedType.isComplexType) {
                    addAnnotation(validationAnnotations.fieldValid())
                }
            }

            else -> {
                if (typeInfo.isComplexType) {
                    addAnnotation(validationAnnotations.fieldValid())
                }
            }
        }
    }
}
