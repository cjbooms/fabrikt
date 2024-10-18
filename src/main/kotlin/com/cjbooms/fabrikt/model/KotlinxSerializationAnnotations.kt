package com.cjbooms.fabrikt.model

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

object KotlinxSerializationAnnotations : SerializationAnnotations {
    /**
     * Supporting "additionalProperties: true" for kotlinx serialization requires additional
     * research and work due to Any type in the map (val properties: MutableMap<String, Any?>)
     *
     * Currently, the generated code does not support additional properties.
     */
    override val supportsAdditionalProperties = false
    override fun addIgnore(propertySpecBuilder: PropertySpec.Builder) =
        propertySpecBuilder // not applicable

    override fun addGetter(funSpecBuilder: FunSpec.Builder) =
        funSpecBuilder // not applicable

    override fun addSetter(funSpecBuilder: FunSpec.Builder) =
        funSpecBuilder // not applicable

    override fun addProperty(propertySpecBuilder: PropertySpec.Builder, oasKey: String) =
        propertySpecBuilder.addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", oasKey).build())

    override fun addParameter(propertySpecBuilder: PropertySpec.Builder, oasKey: String) =
        propertySpecBuilder // not applicable

    override fun addClassAnnotation(typeSpecBuilder: TypeSpec.Builder) =
        typeSpecBuilder.addAnnotation(AnnotationSpec.builder(Serializable::class).build())

    override fun addBasePolymorphicTypeAnnotation(typeSpecBuilder: TypeSpec.Builder, propertyName: String) =
        typeSpecBuilder // not applicable

    override fun addPolymorphicSubTypesAnnotation(typeSpecBuilder: TypeSpec.Builder, mappings: Map<String, TypeName>) =
        typeSpecBuilder // not applicable

    override fun addSubtypeMappingAnnotation(typeSpecBuilder: TypeSpec.Builder, mapping: String) =
        typeSpecBuilder.addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", mapping).build())

    override fun addEnumPropertyAnnotation(propSpecBuilder: PropertySpec.Builder) =
        propSpecBuilder // not applicable

    override fun addEnumConstantAnnotation(enumSpecBuilder: TypeSpec.Builder, enumValue: String) =
        enumSpecBuilder.addAnnotation(AnnotationSpec.builder(SerialName::class).addMember("%S", enumValue).build())
}
