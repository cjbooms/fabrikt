package com.cjbooms.fabrikt.model

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec

sealed interface Annotations {
    fun addIgnore(property: PropertySpec.Builder)

    fun addGetter(`fun`: FunSpec.Builder): FunSpec.Builder

    fun addSetter(`fun`: FunSpec.Builder): FunSpec.Builder

    fun addProperty(property: PropertySpec.Builder, oasKey: String): PropertySpec.Builder

    fun addParameter(property: PropertySpec.Builder, oasKey: String): PropertySpec.Builder
}