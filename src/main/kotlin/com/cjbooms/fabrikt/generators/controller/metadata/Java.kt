package com.cjbooms.fabrikt.generators.controller.metadata

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

object Imports {
    val MIN =
        ClassName("javax.validation.constraints", "Min")

    val MAX =
        ClassName("javax.validation.constraints", "Max")

    val VALID =
        ClassName("javax.validation", "Valid")
}

object JavaXAnnotations {
    fun validBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(Imports.VALID)

    fun fieldValid(): AnnotationSpec = validBuilder().useSiteTarget(AnnotationSpec.UseSiteTarget.GET).build()

    private fun minBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(Imports.MIN)

    private fun maxBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(Imports.MAX)

    fun min(value: Int): AnnotationSpec =
        minBuilder()
            .addMember("%L", value)
            .build()

    fun max(value: Int): AnnotationSpec =
        maxBuilder()
            .addMember("%L", value)
            .build()
}
