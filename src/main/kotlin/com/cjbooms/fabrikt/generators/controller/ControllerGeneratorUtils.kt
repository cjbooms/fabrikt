package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.generators.model.JacksonModelGenerator.Companion.toModelType
import com.cjbooms.fabrikt.model.BodyParameter
import com.cjbooms.fabrikt.model.ControllerType
import com.cjbooms.fabrikt.model.IncomingParameter
import com.cjbooms.fabrikt.model.KotlinTypeInfo
import com.cjbooms.fabrikt.model.RequestParameter
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.reprezen.kaizen.oasparser.model3.Operation
import com.reprezen.kaizen.oasparser.model3.Parameter
import com.reprezen.kaizen.oasparser.model3.Response
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

object ControllerGeneratorUtils {
    /**
     * This maps the OpenAPI response code with a ClassName object.
     * Pulling out the first response. This assumes first is happy path
     * may need to revisit if we want to have conditional responses
     */
    fun Operation.happyPathResponse(basePackage: String): TypeName {
        // Map of response code to nullable name of schema
        val responseDetails = happyPathResponseObject()
        return responseDetails
            .second
            .contentMediaTypes
            .map { it.value?.schema }
            .filterNotNull()
            .firstOrNull()
            ?.let { toModelType(basePackage, KotlinTypeInfo.from(it)) }
            ?: Unit::class.asTypeName()
    }

    fun Operation.happyPathResponseObject(): Pair<Int, Response> {
        val toResponseMapping: Map<Int, Response> = responses
            .filter { it.key != "default" }
            .map { (code, body) ->
                code.toInt() to body
            }.toMap()

        // Happy path, just pull out the http code with the lowest value. Later we may have conditional responses
        val code: Int = toResponseMapping.keys.min() ?: throw IllegalStateException("Could not extract the response for $this")
        val response = toResponseMapping[code]!!

        return code to response
    }

    /**
     * Returns a list of IncomingParameters, ordering logic should be
     * encapsulated here to ensure the order of parameters align between
     * services and controllers
     */
    fun Operation.toIncomingParameters(basePackage: String): List<IncomingParameter> {

        val bodies = requestBody.contentMediaTypes.values
            .map {
                BodyParameter(
                    it.schema.safeName(),
                    toModelType(basePackage, KotlinTypeInfo.from(it.schema)),
                    it.schema
                )
            }

        val parameters = parameters
            .map {
                RequestParameter(
                    it.name,
                    toModelType(basePackage, KotlinTypeInfo.from(it.schema), isRequired(it)),
                    it
                )
            }
            .sortedBy { it.type.isNullable }

        return bodies + parameters
    }

    private fun isRequired(parameter: Parameter): Boolean = parameter.isRequired || parameter.schema.default != null

    fun controllerName(resourceName: String) = "$resourceName${ControllerType.SUFFIX}"

    fun methodName(verb: String, isSingleResource: Boolean) =
        if (isSingleResource) "${verb}ById" else verb
}
