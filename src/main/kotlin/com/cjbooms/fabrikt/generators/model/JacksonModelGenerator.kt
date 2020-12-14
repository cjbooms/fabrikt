package com.cjbooms.fabrikt.generators.model

import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.ClassType
import com.cjbooms.fabrikt.generators.GeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.PropertyUtils.addToClass
import com.cjbooms.fabrikt.generators.TypeFactory.createList
import com.cjbooms.fabrikt.generators.TypeFactory.createMapOfMapsStringToStringAny
import com.cjbooms.fabrikt.generators.TypeFactory.createMapOfStringToType
import com.cjbooms.fabrikt.generators.TypeFactory.createMutableMapOfStringToType
import com.cjbooms.fabrikt.generators.model.JacksonAnnotations.JSON_VALUE
import com.cjbooms.fabrikt.generators.model.JacksonAnnotations.basePolymorphicType
import com.cjbooms.fabrikt.generators.model.JacksonAnnotations.polymorphicSubTypes
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.GeneratedType
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.model.ModelInfo
import com.cjbooms.fabrikt.model.ModelType
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.PropertyInfo
import com.cjbooms.fabrikt.model.PropertyInfo.Companion.HTTP_SETTINGS
import com.cjbooms.fabrikt.model.PropertyInfo.Companion.topLevelProperties
import com.cjbooms.fabrikt.model.SourceApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isComplexTypedAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedObjectDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isReferenceObjectDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.mappingKey
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toMapValueClassName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.kaizen.oasparser.model3.Discriminator
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.io.Serializable

class JacksonModelGenerator(
    private val packages: Packages,
    private val sourceApi: SourceApi,
    private val options: Set<ModelCodeGenOptionType> = emptySet()
) {
    companion object {
        fun toModelType(basePackage: String, typeInfo: KotlinTypeInfo, isRequired: Boolean = true): TypeName {
            val className =
                toClassName(
                    basePackage,
                    typeInfo
                )
            val typeName = when (typeInfo) {
                is KotlinTypeInfo.Array -> createList(
                    toModelType(
                        basePackage,
                        typeInfo.parameterizedType
                    )
                )
                is KotlinTypeInfo.Map ->
                    when (val paramType = typeInfo.parameterizedType) {
                        is KotlinTypeInfo.UntypedProperties -> createMapOfMapsStringToStringAny()
                        is KotlinTypeInfo.UnknownProperties ->
                            createMapOfStringToType(
                                toClassName(
                                    basePackage,
                                    paramType
                                )
                            )
                        is KotlinTypeInfo.TypedProperties ->
                            createMapOfStringToType(
                                toClassName(
                                    basePackage,
                                    paramType
                                )
                            )
                        else -> createMapOfStringToType(
                            toModelType(
                                basePackage,
                                paramType
                            )
                        )
                    }
                is KotlinTypeInfo.UntypedObject -> createMapOfStringToType(className)
                is KotlinTypeInfo.UnknownProperties -> createMutableMapOfStringToType(className)
                is KotlinTypeInfo.UntypedProperties -> createMutableMapOfStringToType(className)
                is KotlinTypeInfo.TypedProperties -> createMutableMapOfStringToType(className)
                else -> className
            }
            return if (isRequired) typeName else typeName.copy(nullable = true)
        }

        private fun toClassName(basePackage: String, typeInfo: KotlinTypeInfo): ClassName =
            if (typeInfo.modelKClass == GeneratedType::class)
                generatedType(
                    basePackage,
                    typeInfo.generatedModelClassName!!
                )
            else typeInfo.modelKClass.asTypeName()

        fun generatedType(basePackage: String, modelName: String) = ClassName(modelsPackage(basePackage), modelName)
    }

    fun generate(): Models {
        val models: MutableSet<TypeSpec> =
            sourceApi.modelInfos
                .filterNot { it.isSimpleType || it.isInlineableMapDefinition }
                .flatMap {
                    if (it.properties.isNotEmpty() || it.typeInfo is KotlinTypeInfo.Enum) {
                        listOf(buildPrimaryModel(it, it.properties)) + buildInLinedModels(it.properties)
                    } else emptyList()
                }.toMutableSet()

        return Models(models.map { ModelType(it, packages.base) })
    }

    private fun buildPrimaryModel(modelInfo: ModelInfo, properties: Collection<PropertyInfo>): TypeSpec {
        val modelName = modelInfo.name.toModelClassName()
        return when {
            modelInfo.isPolymorphicSuperType -> polymorphicSuperType(
                modelName,
                properties,
                modelInfo.schema.discriminator
            )
            modelInfo.isPolymorphicSubType -> polymorphicSubType(
                modelName,
                properties,
                modelInfo.maybeSuperType!!
            )
            modelInfo.typeInfo is KotlinTypeInfo.Enum -> buildEnumClass(modelInfo.typeInfo)
            else -> standardDataClass(modelName, properties)
        }
    }

    private fun buildInLinedModels(topLevelProperties: Collection<PropertyInfo>): List<TypeSpec> =
        topLevelProperties.flatMap {
            when (it) {
                is PropertyInfo.SingleRef ->
                    when {
                        it.schema.isInlinedObjectDefinition() -> it.schema.topLevelProperties(HTTP_SETTINGS)
                            .let { props ->
                                buildInLinedModels(props) +
                                    standardDataClass(it.name.toModelClassName(), props)
                            }
                        it.schema.isReferenceObjectDefinition() -> it.schema.topLevelProperties(HTTP_SETTINGS)
                            .let { props ->
                                buildInLinedModels(props) +
                                    standardDataClass(it.schema.safeName().toModelClassName(), props)
                            }
                        else -> emptySet()
                    }
                is PropertyInfo.MapField -> buildMapModel(it)?.let { mapModel -> setOf(mapModel) } ?: emptySet()
                is PropertyInfo.AdditionalProperties ->
                    if (it.schema.isComplexTypedAdditionalProperties("additionalProperties")) setOf(
                        standardDataClass(it.schema.toMapValueClassName(), it.schema.topLevelProperties(HTTP_SETTINGS))
                    )
                    else emptySet()
                is PropertyInfo.Field ->
                    if (it.typeInfo is KotlinTypeInfo.Enum) setOf(buildEnumClass(it.typeInfo as KotlinTypeInfo.Enum))
                    else emptySet()
                is PropertyInfo.ListField ->
                    it.schema.itemsSchema.let { items ->
                        when {
                            items.isInlinedObjectDefinition() -> items.topLevelProperties(HTTP_SETTINGS).let { props ->
                                buildInLinedModels(props) + standardDataClass(it.name.toModelClassName(), props)
                            }
                            items.isEnumDefinition() ->
                                setOf(buildEnumClass(KotlinTypeInfo.from(items, "items") as KotlinTypeInfo.Enum))
                            else -> emptySet()
                        }
                    }
                else ->
                    emptySet()
            }
        }

    private fun buildEnumClass(enum: KotlinTypeInfo.Enum): TypeSpec {
        val classBuilder = TypeSpec
            .enumBuilder(
                generatedType(
                    packages.base,
                    enum.enumClassName
                )
            )
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("value", String::class)
                    .build()
            )
            .addQuarkusReflectionAnnotation()
        enum.entries.forEach {
            classBuilder.addEnumConstant(
                it.toUpperCase(),
                TypeSpec.anonymousClassBuilder()
                    .addSuperclassConstructorParameter(CodeBlock.of("\"$it\""))
                    .build()
            )
        }
        classBuilder.addProperty(
            PropertySpec.builder("value", String::class)
                .addAnnotation(JSON_VALUE)
                .initializer("value")
                .build()
        )
        return classBuilder.build()
    }

    private fun buildMapModel(mapField: PropertyInfo.MapField): TypeSpec? =
        if (mapField.schema.additionalPropertiesSchema.isComplexTypedAdditionalProperties("additionalProperties"))
            standardDataClass(
                mapField.schema.additionalPropertiesSchema.toMapValueClassName(),
                mapField.schema.additionalPropertiesSchema.topLevelProperties(HTTP_SETTINGS)
            )
        else null

    private fun standardDataClass(modelName: String, properties: Collection<PropertyInfo>): TypeSpec =
        properties.addToClass(
            TypeSpec
                .classBuilder(
                    generatedType(
                        packages.base,
                        modelName
                    )
                )
                .addModifiers(KModifier.DATA)
                .addSerializableInterface()
                .addQuarkusReflectionAnnotation(),
            ClassType.VANILLA_MODEL
        )

    private fun polymorphicSuperType(
        modelName: String,
        properties: Collection<PropertyInfo>,
        discriminator: Discriminator
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(
            generatedType(
                packages.base,
                modelName
            )
        )
            .addModifiers(KModifier.SEALED)
        classBuilder.addAnnotation(basePolymorphicType(discriminator.propertyName))

        val subTypes = sourceApi.modelInfos
            .filter { model ->
                model.schema.allOfSchemas.any { allOfRef ->
                    allOfRef.name == modelName && allOfRef.discriminator == discriminator
                }
            }
            .map {
                discriminator.mappingKey(it.schema) to toModelType(
                    packages.base,
                    KotlinTypeInfo.from(it.schema, it.name)
                )
            }
            .toMap()
        classBuilder.addAnnotation(polymorphicSubTypes(subTypes)).addQuarkusReflectionAnnotation()

        return properties.addToClass(
            classBuilder,
            ClassType.SUPER_MODEL
        )
    }

    private fun polymorphicSubType(
        modelName: String,
        properties: Collection<PropertyInfo>,
        superType: ModelInfo
    ): TypeSpec =
        properties.addToClass(
            TypeSpec
                .classBuilder(
                    generatedType(
                        packages.base,
                        modelName
                    )
                )
                .addModifiers(KModifier.DATA)
                .addSerializableInterface()
                .addQuarkusReflectionAnnotation()
                .superclass(
                    toModelType(
                        packages.base,
                        KotlinTypeInfo.from(superType.schema, superType.name)
                    )
                ),
            ClassType.SUB_MODEL
        )

    private fun Collection<PropertyInfo>.addToClass(classBuilder: TypeSpec.Builder, classType: ClassType): TypeSpec {
        val constructorBuilder = FunSpec.constructorBuilder()
        this.forEach {
            it.addToClass(
                toModelType(
                    packages.base,
                    it.typeInfo,
                    it.isRequired
                ),
                toClassName(
                    packages.base,
                    it.typeInfo
                ),
                classBuilder,
                constructorBuilder,
                classType
            )
        }
        return classBuilder.primaryConstructor(constructorBuilder.build()).build()
    }

    private fun TypeSpec.Builder.addSerializableInterface(): TypeSpec.Builder {
        if (options.any { it == ModelCodeGenOptionType.JAVA_SERIALIZATION })
            this.addSuperinterface(Serializable::class)
        return this
    }

    private fun TypeSpec.Builder.addQuarkusReflectionAnnotation(): TypeSpec.Builder {
        if (options.any { it == ModelCodeGenOptionType.QUARKUS_REFLECTION })
            this.addAnnotation(
                AnnotationSpec.builder("RegisterForReflection".toClassName("io.quarkus.runtime.annotations")).build()
            )
        return this
    }
}
