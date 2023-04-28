package com.cjbooms.fabrikt.model

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isNotDefined
import com.cjbooms.fabrikt.util.NormalisedString.pascalCase
import com.cjbooms.fabrikt.util.YamlUtils
import com.cjbooms.fabrikt.validation.ValidationError
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

data class SchemaInfo(val name: String, val schema: Schema, private val enclosingName: String = "") {
    val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, name, enclosingName)
}

data class SourceApi(
    private val rawApiSpec: String,
    val baseDir: Path = Paths.get("").toAbsolutePath()
) {
    companion object {
        private val logger = Logger.getGlobal()

        fun create(
            baseApi: String,
            apiFragments: Collection<String>,
            baseDir: Path = Paths.get("").toAbsolutePath()
        ): SourceApi {
            val combinedApi =
                apiFragments.fold(baseApi) { acc: String, fragment -> YamlUtils.mergeYamlTrees(acc, fragment) }
            return SourceApi(combinedApi, baseDir)
        }
    }

    val openApi3: OpenApi3 = YamlUtils.parseOpenApi(rawApiSpec, baseDir)
    val allSchemas: List<SchemaInfo>

    init {
        validateSchemaObjects(openApi3).let {
            if (it.isNotEmpty()) throw ParameterException("Invalid models or api file:\n${it.joinToString("\n\t")}")
        }

        val componentSchemas = openApi3.schemas.entries.map { it.key to it.value }
        val componentParameters = openApi3.parameters.entries.map { it.key to it.value.schema }
        val componentRequests = openApi3.requestBodies.entries.flatMap {  request ->
            request.value.contentMediaTypes.entries.map { content ->
                request.key to content.value.schema
            }
        }
        val componentResponses =
            openApi3.responses.entries.flatMap {  response ->
                response.value.contentMediaTypes.entries.map { content ->
                    response.key to content.value.schema
                }
            }
        val componentSchemaInfos = (componentSchemas + componentParameters + componentRequests + componentResponses).map { (key, schema) ->
            SchemaInfo(key, schema)
        }

        val pathParameters = mutableListOf<Pair<String, Schema>>()
        val operationsRequests = mutableListOf<Pair<String, Schema>>()
        val operationResponses = mutableListOf<Pair<String, Schema>>()
        val operationParameters = mutableListOf<Pair<String, Schema>>()
        openApi3.paths.forEach { (pathName, path) ->
            pathParameters += path.parameters.map { "$pathName-${it.name}-Parameter" to it.schema }
            path.operations.forEach { (operationName, operation) ->
                operationsRequests += operation.requestBody.contentMediaTypes.map { "$pathName-$operationName-${it.key}-Request" to it.value.schema }
                operation.responses.forEach { (responseName, response) ->
                    operationResponses += response.contentMediaTypes.map { "$pathName-$operationName-$responseName-${it.key}-Response" to it.value.schema }
                }
                operationParameters += operation.parameters.map { "$pathName-$operationName-${it.name}-Parameter" to it.schema }
            }
        }
        val pathSchemaInfos = (pathParameters + operationsRequests + operationResponses + operationParameters).map { (key, schema) ->
            SchemaInfo(key, schema, key.pascalCase())
        }

        println()
        allSchemas = componentSchemaInfos + pathSchemaInfos
    }

    private fun validateSchemaObjects(api: OpenApi3): List<ValidationError> {
        val schemaErrors = api.schemas.entries.fold(emptyList<ValidationError>()) { errors, entry ->
            val name = entry.key
            val schema = entry.value
            if (schema.type == OasType.Object.type && (
                schema.oneOfSchemas?.isNotEmpty() == true ||
                    schema.allOfSchemas?.isNotEmpty() == true ||
                    schema.anyOfSchemas?.isNotEmpty() == true
                )
            )
                errors + listOf(
                    ValidationError(
                        "'$name' schema contains an invalid combination of properties and `oneOf | anyOf | allOf`. " +
                            "Do not use properties and a combiner at the same level."
                    )
                )
            else if (schema.type == OasType.Object.type && schema.oneOfSchemas?.isNotEmpty() == true)
                errors + listOf(ValidationError("The $name object contains invalid use of both properties and `oneOf`."))
            else if (schema.type == OasType.Object.type && schema.oneOfSchemas?.isNotEmpty() == true)
                errors + listOf(ValidationError("The $name object contains invalid use of both properties and `oneOf`."))
            else if (schema.type == null && schema.properties?.isNotEmpty() == true) {
                logger.warning("Schema '$name' has 'type: null' but defines properties. Assuming: 'type: object'")
                errors
            } else errors
        }

        return api.schemas.map { it.value.properties }.flatMap { it.entries }
            .fold(schemaErrors) { lst, entry ->
                val name = entry.key
                val schema = entry.value
                if (schema.type == OasType.Array.type && schema.itemsSchema.isNotDefined()) {
                    lst + listOf(ValidationError("Array type '$name' cannot be parsed to a Schema. Check your input"))
                } else if (schema.isNotDefined()) {
                    lst + listOf(ValidationError("Property '$name' cannot be parsed to a Schema. Check your input"))
                } else {
                    lst
                }
            }
    }
}
