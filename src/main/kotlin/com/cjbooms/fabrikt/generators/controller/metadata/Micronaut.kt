package com.cjbooms.fabrikt.generators.controller.metadata

import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports.DATE_TIME_FORMAT
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName

object MicronautImports {

    private object Packages {
        const val MICRONAUT_BASE = "io.micronaut"

        const val MICRONAUT_HTTP = "$MICRONAUT_BASE.http"

        const val MICRONAUT_HTTP_ANNOTATION = "$MICRONAUT_HTTP.annotation"

        const val MICRONAUT_AUTHENTICATION = "$MICRONAUT_BASE.security.authentication"
    }

    val CONSUMES = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Consumes")
    val PRODUCES = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Produces")
    val RESPONSE = ClassName(Packages.MICRONAUT_HTTP, "HttpResponse")

    val CONTROLLER = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Controller")
    val STATUS = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Status")
    val BODY = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Body")
    val HEADER = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Header")
    val QUERY_VALUE = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "QueryValue")
    val PATH_VARIABLE = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "PathVariable")
    val AUTHENTICATION = ClassName(Packages.MICRONAUT_AUTHENTICATION, "Authentication")

    object HttpMethods {
        val GET = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Get")
        val POST = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Post")
        val PUT = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Put")
        val PATCH = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Patch")
        val DELETE = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Delete")
        val OPTIONS = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Options")
        val HEAD = ClassName(Packages.MICRONAUT_HTTP_ANNOTATION, "Head")

        fun byName(name: String) =
            when (name.toUpperCase()) {
                "GET" -> GET
                "POST" -> POST
                "PUT" -> PUT
                "PATCH" -> PATCH
                "DELETE" -> DELETE
                "OPTIONS" -> OPTIONS
                "HEAD" -> HEAD
                else -> throw IllegalArgumentException(name)
            }
    }

    val DATE_TIME_FORMAT = ClassName("org.springframework.format.annotation", "DateTimeFormat")

    object DateTimeFormat {
        val ISO_DATE = "${DATE_TIME_FORMAT.simpleName}.ISO.DATE"

        val ISO_DATE_TIME = "${DATE_TIME_FORMAT.simpleName}.ISO.DATE_TIME"
    }
}
