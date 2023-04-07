package com.cjbooms.fabrikt.generators.model

import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.ClassSettings
import com.cjbooms.fabrikt.generators.GeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.JavaxValidationAnnotations
import com.cjbooms.fabrikt.generators.PropertyUtils.addToClass
import com.cjbooms.fabrikt.generators.PropertyUtils.isNullable
import com.cjbooms.fabrikt.generators.TypeFactory.createList
import com.cjbooms.fabrikt.generators.TypeFactory.createMapOfMapsStringToStringAny
import com.cjbooms.fabrikt.generators.TypeFactory.createMapOfStringToType
import com.cjbooms.fabrikt.generators.TypeFactory.createMutableMapOfMapsStringToStringType
import com.cjbooms.fabrikt.generators.TypeFactory.createMutableMapOfStringToType
import com.cjbooms.fabrikt.generators.ValidationAnnotations
import com.cjbooms.fabrikt.generators.model.JacksonMetadata.JSON_VALUE
import com.cjbooms.fabrikt.generators.model.JacksonMetadata.basePolymorphicType
import com.cjbooms.fabrikt.generators.model.JacksonMetadata.polymorphicSubTypes
import com.cjbooms.fabrikt.model.*
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.PropertyInfo.Companion.HTTP_SETTINGS
import com.cjbooms.fabrikt.model.PropertyInfo.Companion.topLevelProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isComplexTypedAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedObjectDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlinedTypedAdditionalProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isOneOfPolymorphicTypes
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSubType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSimpleType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.mappingKeys
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toMapValueClassName
import com.cjbooms.fabrikt.util.KaizenParserExtensions.toModelClassName
import com.cjbooms.fabrikt.util.NormalisedString.toEnumName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.jsonoverlay.Overlay
import com.reprezen.kaizen.oasparser.OpenApi3Parser
import com.reprezen.kaizen.oasparser.model3.Discriminator
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import com.squareup.kotlinpoet.*
import java.io.Serializable
import java.net.MalformedURLException
import java.net.URL

class JacksonModelGenerator(
    private val packages: Packages,
    private val sourceApi: SourceApi,
    private val options: Set<ModelCodeGenOptionType> = emptySet(),
    private val validationAnnotations: ValidationAnnotations = JavaxValidationAnnotations
) {
    companion object {
        fun toModelType(basePackage: String, typeInfo: KotlinTypeInfo, isNullable: Boolean = false): TypeName {
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
                        is KotlinTypeInfo.UntypedObjectAdditionalProperties -> createMapOfMapsStringToStringAny()
                        is KotlinTypeInfo.UnknownAdditionalProperties ->
                            createMapOfStringToType(
                                toClassName(
                                    basePackage,
                                    paramType
                                )
                            )
                        is KotlinTypeInfo.GeneratedTypedAdditionalProperties ->
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
                is KotlinTypeInfo.UnknownAdditionalProperties -> createMutableMapOfStringToType(className)
                is KotlinTypeInfo.UntypedObjectAdditionalProperties -> createMutableMapOfStringToType(className)
                is KotlinTypeInfo.GeneratedTypedAdditionalProperties -> createMutableMapOfStringToType(className)
                is KotlinTypeInfo.MapTypeAdditionalProperties -> createMutableMapOfMapsStringToStringType(
                    toModelType(
                        basePackage,
                        typeInfo.parameterizedType
                    )
                )
                else -> className
            }
            return if (isNullable) typeName.copy(nullable = true) else typeName
        }

        private fun toClassName(basePackage: String, typeInfo: KotlinTypeInfo): ClassName =
            when {
                typeInfo.modelKClass == GeneratedType::class ->
                    generatedType(basePackage, typeInfo.generatedModelClassName!!)
                typeInfo is KotlinTypeInfo.MapTypeAdditionalProperties ->
                    generatedType(basePackage, typeInfo.parameterizedType.generatedModelClassName!!)
                else -> typeInfo.modelKClass.asTypeName()
            }

        fun generatedType(basePackage: String, modelName: String) = ClassName(modelsPackage(basePackage), modelName)
    }

    private val externalApiSchemas = mutableMapOf<URL, MutableSet<String>>()

    fun generate(): Models {
        val models: MutableSet<TypeSpec> = createModels(sourceApi.openApi3, sourceApi.allSchemas)
        externalApiSchemas.forEach { externalReferences ->
            val api = OpenApi3Parser().parse(externalReferences.key)
            val schemas = api.schemas.entries.map { it.key to it.value }
                .map { (key, schema) -> SchemaInfo(key, schema) }
                .filter { apiSchema -> externalReferences.value.contains(apiSchema.name) }
            val externalModels = createModels(api, schemas)
            externalModels.forEach { additionalModel ->
                if (models.none { it.name == additionalModel.name }) models.add(additionalModel)
            }
        }
        return Models(models.map { ModelType(it, packages.base) })
    }

    private fun createModels(api: OpenApi3, schemas: List<SchemaInfo>) = schemas
        .filterNot { it.schema.isSimpleType() }
        .filterNot { it.schema.isOneOfPolymorphicTypes() }
        .flatMap {
            val properties = it.schema.topLevelProperties(HTTP_SETTINGS, it.schema)
            if (properties.isNotEmpty() || it.typeInfo is KotlinTypeInfo.Enum) {
                val primaryModel = buildPrimaryModel(api, it, properties, schemas)
                val inlinedModels = buildInLinedModels(properties, it.schema, it.schema.getDocumentUrl())
                listOf(primaryModel) + inlinedModels
            } else emptyList()
        }.toMutableSet()

    private fun buildPrimaryModel(
        api: OpenApi3,
        schemaInfo: SchemaInfo,
        properties: Collection<PropertyInfo>,
        allSchemas: List<SchemaInfo>
    ): TypeSpec {
        val modelName = schemaInfo.name.toModelClassName()
        return when {
            schemaInfo.schema.isPolymorphicSuperType() -> polymorphicSuperType(
                modelName,
                properties,
                schemaInfo.schema.discriminator,
                schemaInfo.schema.extensions,
                allSchemas
            )
            schemaInfo.schema.isPolymorphicSubType(api) -> polymorphicSubType(
                modelName,
                properties,
                schemaInfo.schema.getSuperType(api)!!.let { SchemaInfo(it.name, it) },
                schemaInfo.schema.extensions,
            )
            schemaInfo.typeInfo is KotlinTypeInfo.Enum -> buildEnumClass(schemaInfo.typeInfo)
            else -> standardDataClass(modelName, properties, schemaInfo.schema.extensions)
        }
    }

    private fun buildInLinedModels(
        topLevelProperties: Collection<PropertyInfo>,
        enclosingSchema: Schema,
        apiDocUrl: String
    ): List<TypeSpec> = topLevelProperties.flatMap {
        val enclosingModelName = enclosingSchema.toModelClassName()
        if (it.schema.isInExternalDocument(apiDocUrl)) {
            it.schema.captureMissingExternalSchemas(apiDocUrl)
            emptySet()
        } else
            when (it) {
                is PropertyInfo.ObjectInlinedField -> {
                    val props = it.schema.topLevelProperties(HTTP_SETTINGS, enclosingSchema)
                    val currentModel = standardDataClass(
                        it.name.toModelClassName(enclosingModelName),
                        props,
                        it.schema.extensions
                    )
                    val inlinedModels = buildInLinedModels(props, enclosingSchema, apiDocUrl)
                    inlinedModels + currentModel
                }
                is PropertyInfo.ObjectRefField -> emptySet() // Not an inlined definition, so do nothing
                is PropertyInfo.MapField ->
                    buildMapModel(it)?.let { mapModel -> setOf(mapModel) } ?: emptySet()
                is PropertyInfo.AdditionalProperties ->
                    if (it.schema.isComplexTypedAdditionalProperties("additionalProperties")) setOf(
                        standardDataClass(
                            if (it.schema.isInlinedTypedAdditionalProperties()) it.schema.toMapValueClassName() else it.schema.toModelClassName(),
                            it.schema.topLevelProperties(HTTP_SETTINGS, enclosingSchema),
                            it.schema.extensions
                        )
                    )
                    else emptySet()
                is PropertyInfo.Field ->
                    if (it.typeInfo is KotlinTypeInfo.Enum) setOf(buildEnumClass(it.typeInfo as KotlinTypeInfo.Enum))
                    else emptySet()
                is PropertyInfo.ListField ->
                    it.schema.itemsSchema.let { items ->
                        when {
                            items.isInlinedObjectDefinition() ->
                                items.topLevelProperties(HTTP_SETTINGS, enclosingSchema).let { props ->
                                    buildInLinedModels(props, enclosingSchema, apiDocUrl) + standardDataClass(
                                        it.name.toModelClassName(enclosingModelName), props, it.schema.extensions
                                    )
                                }
                            items.isInlinedEnumDefinition() ->
                                setOf(
                                    buildEnumClass(
                                        KotlinTypeInfo.from(items, "items", enclosingModelName) as KotlinTypeInfo.Enum
                                    )
                                )
                            else -> emptySet()
                        }
                    }
                is PropertyInfo.OneOfAny -> emptySet()
            }
    }

    private fun Schema.captureMissingExternalSchemas(apiDocUrl: String, depth: Int = 0) {
        nestedSchemas().forEach { schema ->
            val docUrl = schema.getDocumentUrl()
            if (docUrl != apiDocUrl) {
                try {
                    externalApiSchemas.getOrPut(URL(docUrl)) { mutableSetOf() }.add(schema.safeName())
                } catch (ex: MalformedURLException) {
                    // skip
                }
            }
            if (depth < 10) schema.captureMissingExternalSchemas(apiDocUrl, depth + 1)
        }
    }

    private fun Schema.isInExternalDocument(apiDocUrl: String): Boolean {
        val docUrl = getDocumentUrl()
        return docUrl != apiDocUrl
    }

    private fun Schema.getDocumentUrl(): String {
        val positionInfo = Overlay.of(this).positionInfo?.orElse(null)
        return positionInfo?.documentUrl ?: "Not Found"
    }

    private fun Schema.nestedSchemas() =
        (
            allOfSchemas + anyOfSchemas + oneOfSchemas + itemsSchema + additionalPropertiesSchema +
                this + this.properties.map { it.value }
            )
            .filterNotNull()
            .filter { Overlay.of(it).isPresent }

    private fun buildEnumClass(enum: KotlinTypeInfo.Enum): TypeSpec {
        val enumType = generatedType(packages.base, enum.enumClassName)
        val classBuilder = TypeSpec
            .enumBuilder(enumType)
            .primaryConstructor(
                FunSpec.constructorBuilder()
                    .addParameter("value", String::class)
                    .build()
            )
            .addQuarkusReflectionAnnotation()
            .addMicronautIntrospectedAnnotation()
            .addMicronautReflectionAnnotation()
        enum.entries.forEach {
            classBuilder.addEnumConstant(
                it.toEnumName(),
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
        val companion = TypeSpec.companionObjectBuilder()
            .addProperty(
                PropertySpec.builder("mapping", createMapOfStringToType(enumType))
                    .initializer("values().associateBy(%T::value)", enumType)
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addFunction(
                FunSpec.builder("fromValue")
                    .addParameter(ParameterSpec.builder("value", String::class).build())
                    .returns(enumType.copy(nullable = true))
                    .addStatement("return mapping[value]")
                    .build()
            )
            .build()
        return classBuilder.addType(companion).build()
    }

    private fun buildMapModel(mapField: PropertyInfo.MapField): TypeSpec? =
        if (mapField.schema.additionalPropertiesSchema.isComplexTypedAdditionalProperties("additionalProperties")) {
            val schema = mapField.schema.additionalPropertiesSchema
            standardDataClass(
                if (schema.isInlinedTypedAdditionalProperties()) schema.toMapValueClassName() else schema.toModelClassName(),
                mapField.schema.additionalPropertiesSchema.topLevelProperties(HTTP_SETTINGS),
                mapField.schema.extensions
            )
        } else null

    private fun standardDataClass(
        modelName: String,
        properties: Collection<PropertyInfo>,
        extensions: Map<String, Any>,
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(generatedType(packages.base, modelName))
            .addSerializableInterface()
            .addQuarkusReflectionAnnotation()
            .addMicronautIntrospectedAnnotation()
            .addMicronautReflectionAnnotation()
            .addCompanionObject()
        properties.addToClass(
            classBuilder,
            ClassSettings(ClassSettings.PolymorphyType.NONE, extensions.hasJsonMergePatchExtension)
        )
        return classBuilder.build()
    }

    private fun polymorphicSuperType(
        modelName: String,
        properties: Collection<PropertyInfo>,
        discriminator: Discriminator,
        extensions: Map<String, Any>,
        allSchemas: List<SchemaInfo>,
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(generatedType(packages.base, modelName))
            .addModifiers(KModifier.SEALED)
            .addAnnotation(basePolymorphicType(discriminator.propertyName))

        val subTypes = allSchemas
            .filter { model ->
                model.schema.allOfSchemas.any { allOfRef ->
                    allOfRef.name?.toModelClassName() == modelName && allOfRef.discriminator == discriminator
                }
            }
        val mappings = subTypes.flatMap { schemaInfo ->
            discriminator.mappingKeys(schemaInfo.schema).map {
                it to toModelType(packages.base, KotlinTypeInfo.from(schemaInfo.schema, schemaInfo.name))
            }
        }.toMap()
        val maybeEnumDiscriminator = properties
            .firstOrNull { it.name == discriminator.propertyName }?.typeInfo as? KotlinTypeInfo.Enum

        classBuilder.addAnnotation(polymorphicSubTypes(mappings, maybeEnumDiscriminator))
            .addQuarkusReflectionAnnotation()
            .addMicronautIntrospectedAnnotation()
            .addMicronautReflectionAnnotation()

        properties.addToClass(
            classBuilder,
            ClassSettings(ClassSettings.PolymorphyType.SUPER, extensions.hasJsonMergePatchExtension)
        )

        return classBuilder.build()
    }

    private fun polymorphicSubType(
        modelName: String,
        properties: Collection<PropertyInfo>,
        superType: SchemaInfo,
        extensions: Map<String, Any>,
    ): TypeSpec {
        val classBuilder = TypeSpec.classBuilder(generatedType(packages.base, modelName))
            .addSerializableInterface()
            .addQuarkusReflectionAnnotation()
            .addMicronautIntrospectedAnnotation()
            .addMicronautReflectionAnnotation()
            .addCompanionObject()
            .superclass(
                toModelType(packages.base, KotlinTypeInfo.from(superType.schema, superType.name))
            )
        properties.addToClass(
            classBuilder,
            ClassSettings(ClassSettings.PolymorphyType.SUB, extensions.hasJsonMergePatchExtension)
        )
        return classBuilder.build()
    }

    private fun Collection<PropertyInfo>.addToClass(
        classBuilder: TypeSpec.Builder,
        classType: ClassSettings
    ): TypeSpec.Builder {
        val constructorBuilder = FunSpec.constructorBuilder()
        this.forEach {
            it.addToClass(
                toModelType(
                    packages.base,
                    it.typeInfo,
                    it.isNullable()
                ),
                toClassName(
                    packages.base,
                    it.typeInfo
                ),
                classBuilder,
                constructorBuilder,
                classType,
                validationAnnotations
            )
        }
        if (constructorBuilder.parameters.isNotEmpty() && classBuilder.modifiers.isEmpty()) classBuilder.addModifiers(KModifier.DATA)
        return classBuilder.primaryConstructor(constructorBuilder.build())
    }

    private fun TypeSpec.Builder.addSerializableInterface(): TypeSpec.Builder {
        if (options.any { it == ModelCodeGenOptionType.JAVA_SERIALIZATION })
            this.addSuperinterface(Serializable::class)
        return this
    }

    private fun TypeSpec.Builder.addQuarkusReflectionAnnotation(): TypeSpec.Builder =
        this.addOptionalAnnotation(
            ModelCodeGenOptionType.QUARKUS_REFLECTION,
            "RegisterForReflection".toClassName("io.quarkus.runtime.annotations")
        )

    private fun TypeSpec.Builder.addMicronautIntrospectedAnnotation(): TypeSpec.Builder =
        this.addOptionalAnnotation(
            ModelCodeGenOptionType.MICRONAUT_INTROSPECTION,
            "Introspected".toClassName("io.micronaut.core.annotation")
        )

    private fun TypeSpec.Builder.addMicronautReflectionAnnotation(): TypeSpec.Builder =
        this.addOptionalAnnotation(
            ModelCodeGenOptionType.MICRONAUT_REFLECTION,
            "ReflectiveAccess".toClassName("io.micronaut.core.annotation")
        )

    private fun TypeSpec.Builder.addCompanionObject(): TypeSpec.Builder {
        if(options.any { it == ModelCodeGenOptionType.INCLUDE_COMPANION_OBJECT })
            this.addType(TypeSpec.companionObjectBuilder().build())
        return this
    }

    private fun TypeSpec.Builder.addOptionalAnnotation(
        optionType: ModelCodeGenOptionType,
        type: ClassName
    ): TypeSpec.Builder {
        if (options.any { it == optionType })
            this.addAnnotation(
                AnnotationSpec.builder(type).build()
            )
        return this
    }
}

private val Map<String, Any>.hasJsonMergePatchExtension
    get() = this.containsKey("x-json-merge-patch")

