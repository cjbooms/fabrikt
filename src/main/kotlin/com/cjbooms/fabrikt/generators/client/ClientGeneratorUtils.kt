package com.cjbooms.fabrikt.generators.client

import com.cjbooms.fabrikt.configurations.Packages
import com.cjbooms.fabrikt.generators.GeneratorUtils.getPrimaryAcceptMediaType
import com.cjbooms.fabrikt.generators.GeneratorUtils.toClassName
import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator.Companion.toModelType
import com.cjbooms.fabrikt.model.ClientType
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.reprezen.kaizen.oasparser.model3.Operation
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

object ClientGeneratorUtils {
    /**
     * It resolves the API operation response to its body type. It iterates over all non-default responses
     * by filtering those ones with content media. If multiple medias are found, then it will resolve to the schema
     * reference of the first media type found, otherwise it'll resolve to Unit by assuming no response body
     * for the given operation.
     */
    fun Operation.toClientReturnType(packages: Packages): TypeName {
        val returnType = this.getPrimaryAcceptMediaType()?.let {
            toModelType(
                packages.base,
                KotlinTypeInfo.from(it.value.schema)
            )
        } ?: Unit::class.asTypeName()
        return "ApiResponse".toClassName(packages.client)
            .parameterizedBy(returnType.copy(nullable = true))
    }

    fun simpleClientName(resourceName: String) = "$resourceName${ClientType.SIMPLE_CLIENT_SUFFIX}"

    fun enhancedClientName(resourceName: String) = "$resourceName${ClientType.ENHANCED_CLIENT_SUFFIX}"
}
