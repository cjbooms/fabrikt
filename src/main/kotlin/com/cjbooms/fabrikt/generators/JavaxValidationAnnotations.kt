package com.cjbooms.fabrikt.generators

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

abstract class ValidationAnnotations(packageName: String) {
    private val minClass = ClassName("$packageName.constraints", "Min")
    private val maxClass = ClassName("$packageName.constraints", "Max")
    private val validClass = ClassName(packageName, "Valid")
    private val notNullClass = ClassName("$packageName.constraints", "NotNull")
    private val patternClass = ClassName("$packageName.constraints", "Pattern")
    private val sizeClass = ClassName("$packageName.constraints", "Size")
    private val decimalMinClass = ClassName("$packageName.constraints", "DecimalMin")
    private val decimalMaxClass = ClassName("$packageName.constraints", "DecimalMax")

    val nonNullAnnotation = AnnotationSpec
        .builder(notNullClass)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .build()

    fun fieldValid() = AnnotationSpec
        .builder(validClass)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .build()

    fun min(value: Int) = AnnotationSpec
        .builder(minClass)
        .addMember("%L", value)
        .build()

    fun max(value: Int) = AnnotationSpec
        .builder(maxClass)
        .addMember("%L", value)
        .build()

    fun regexPattern(pattern: String) = AnnotationSpec
        .builder(patternClass)
        .addMember("regexp = %S", pattern)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .build()

    fun lengthRestriction(min: Int?, max: Int?): AnnotationSpec {
        val specBuilder = AnnotationSpec.builder(sizeClass).useSiteTarget(AnnotationSpec.UseSiteTarget.GET)

        min?.let { specBuilder.addMember("min = %L", it) }
        max?.let { specBuilder.addMember("max = %L", it) }

        return specBuilder.build()
    }

    fun minRestriction(min: Number, exclusive: Boolean) = AnnotationSpec
        .builder(decimalMinClass)
        .addMember("value = %S", min.toString())
        .addMember("inclusive = %L", !exclusive)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .build()

    fun maxRestriction(max: Number, exclusive: Boolean) = AnnotationSpec
        .builder(decimalMaxClass)
        .addMember("value = %S", max.toString())
        .addMember("inclusive = %L", !exclusive)
        .useSiteTarget(AnnotationSpec.UseSiteTarget.GET)
        .build()
}

object JavaxValidationAnnotations: ValidationAnnotations("javax.validation")

object JakartaAnnotations: ValidationAnnotations("jakarta.validation")
