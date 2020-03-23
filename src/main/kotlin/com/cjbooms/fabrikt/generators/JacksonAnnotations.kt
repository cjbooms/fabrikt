package com.cjbooms.fabrikt.generators

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName

object JacksonAnnotations {
    private val JSON_PROPERTY_CLASS = ClassName("com.fasterxml.jackson.annotation", "JsonProperty")
    private val JSON_VALUE_CLASS = ClassName("com.fasterxml.jackson.annotation", "JsonValue")
    private val JSON_TYPE_INFO_CLASS = ClassName("com.fasterxml.jackson.annotation", "JsonTypeInfo")
    private val JSON_SUB_TYPES_CLASS = ClassName("com.fasterxml.jackson.annotation", "JsonSubTypes")
    private val JSON_ANY_SETTER = ClassName("com.fasterxml.jackson.annotation", "JsonAnySetter")

    val JSON_VALUE = AnnotationSpec.builder(JSON_VALUE_CLASS).build()

    fun jacksonPropertyAnnotation(name: String) = AnnotationSpec
        .builder(JSON_PROPERTY_CLASS)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .addMember("%S", name).build()

    fun jacksonParameterAnnotation(name: String) = AnnotationSpec
        .builder(JSON_PROPERTY_CLASS)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.PARAM)
        .addMember("%S", name).build()

    val anySetter = AnnotationSpec.builder(JSON_ANY_SETTER).build()

    fun basePolymorphicType(discriminatorFieldName: String) = AnnotationSpec
        .builder(JSON_TYPE_INFO_CLASS)
        .addMember(
            "use = %T.Id.NAME",
            JSON_TYPE_INFO_CLASS
        )
        .addMember(
            "include = %T.As.EXISTING_PROPERTY",
            JSON_TYPE_INFO_CLASS
        )
        .addMember("property = %S", discriminatorFieldName)
        .addMember("visible = true")
        .build()

    fun polymorphicSubTypes(typePairs: Map<String, TypeName>): AnnotationSpec {
        val codeBuilder = CodeBlock.builder()
        typePairs.forEach { (name, type) ->
            if (codeBuilder.isNotEmpty()) codeBuilder.add(",")
            codeBuilder.add(
                "%T.Type(value = %T::class, name = %S)",
                JSON_SUB_TYPES_CLASS, type, name
            )
        }

        return AnnotationSpec.builder(JSON_SUB_TYPES_CLASS)
            .addMember(codeBuilder.build()).build()
    }
}
