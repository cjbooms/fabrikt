package com.cjbooms.fabrikt.generators.client

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.getPrimaryContentMediaType
import com.cjbooms.fabrikt.generators.GeneratorUtils.hasMultipleResponseSchemas
import com.cjbooms.fabrikt.generators.GeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator.Companion.toModelType
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.fasterxml.jackson.databind.JsonNode
import com.reprezen.kaizen.oasparser.model3.Operation
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

object ClientGeneratorUtils {
    const val ACCEPT_HEADER_NAME = "Accept"
    const val ACCEPT_HEADER_VARIABLE_NAME = "acceptHeader"
    const val ADDITIONAL_HEADERS_PARAMETER_NAME = "additionalHeaders"

    /**
     * Gives the Kotlin return type for an API call based on the Content-Types specified in the Operation.
     * If multiple media types are found, but their response schema is the same, then the first media type is used and kotlin model for the schema is returned.
     * If there are several possible response schemas, then the return type is JsonNode, so they are all covered.
     * If no response body is found, Unit is returned.
     */
    fun Operation.toClientReturnType(packages: Packages): TypeName {
        val returnType =
                if (hasMultipleResponseSchemas()) {
                    JsonNode::class.asTypeName()
                } else {
                    this.getPrimaryContentMediaType()?.let {
                        toModelType(
                                packages.base,
                                KotlinTypeInfo.from(it.value.schema)
                        )
                    } ?: Unit::class.asTypeName()
                }

        return "ApiResponse".toClassName(packages.client)
            .parameterizedBy(returnType.copy(nullable = true))
    }

    fun simpleClientName(resourceName: String) = "$resourceName${ClientType.SIMPLE_CLIENT_SUFFIX}"

    fun enhancedClientName(resourceName: String) = "$resourceName${ClientType.ENHANCED_CLIENT_SUFFIX}"
}
