package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

object ExperimentalApiUtils {
    val experimentalApiMap = mapOf(
        KotlinTypeInfo.KotlinUuid.modelKClass.asTypeName() to listOf(
            ClassName("kotlin.uuid", "ExperimentalUuidApi"),
        )
    )

    public fun getNeededOptIns(
        typeInfo: KotlinTypeInfo,
    ): Set<AnnotationSpec> =
        getNeededOptIns(typeInfo.modelKClass.asTypeName())

    public fun getNeededOptIns(typeName: TypeName): Set<AnnotationSpec> =
        experimentalApiMap[typeName.copy(nullable = false)]?.map {
            val optInAnnotationSpec = AnnotationSpec.builder(ClassName("kotlin", "OptIn"))
                .addMember("%T::class", it)
                .build()
            optInAnnotationSpec
        }?.toSet() ?: emptySet()
}