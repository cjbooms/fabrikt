package com.cjbooms.fabrikt.generators.client.metadata

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

object OpenFeignImports {

    private object Packages {
        const val FEIGN = "feign"
    }

    val REQUEST_LINE = ClassName(Packages.FEIGN, "RequestLine")

    val HEADERS = ClassName(Packages.FEIGN, "Headers")

    val HEADER_MAP = ClassName(Packages.FEIGN, "HeaderMap")

    val PARAM = ClassName(Packages.FEIGN, "Param")
}

object OpenFeignAnnotations {
    val HEADER_MAP: AnnotationSpec =
        AnnotationSpec
            .builder(OpenFeignImports.HEADER_MAP)
            .build()

    fun requestLineBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(OpenFeignImports.REQUEST_LINE)

    fun headersBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(OpenFeignImports.HEADERS)

    fun paramBuilder(): AnnotationSpec.Builder =
        AnnotationSpec
            .builder(OpenFeignImports.PARAM)
}
