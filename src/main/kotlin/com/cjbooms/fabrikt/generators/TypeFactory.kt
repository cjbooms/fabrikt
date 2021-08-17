package com.cjbooms.fabrikt.generators

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName

object TypeFactory {

    fun createMapOfMapsStringToStringAny() =
        Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            Map::class.asTypeName().parameterizedBy(
                String::class.asTypeName(),
                Any::class.asTypeName()
            )
        )

    fun createMutableMapOfMapsStringToStringType(type: TypeName) =
        ClassName("kotlin.collections", "MutableMap").parameterizedBy(
            String::class.asTypeName(),
            Map::class.asTypeName().parameterizedBy(
                String::class.asTypeName(),
                type
            )
        )

    fun createMutableMapOfStringToType(type: TypeName) =
        ClassName("kotlin.collections", "MutableMap").parameterizedBy(
            String::class.asTypeName(),
            type
        )

    fun createMapOfStringToType(type: TypeName) =
        Map::class.asClassName().parameterizedBy(
            String::class.asTypeName(),
            type
        )

    fun createList(clazz: TypeName) =
        List::class.asClassName().parameterizedBy(clazz)
}
