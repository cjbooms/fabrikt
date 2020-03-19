package com.cjbooms.fabrikt.model

import com.beust.jcommander.ParameterException
import com.cjbooms.fabrikt.model.ModelInfo.Companion.modelInfosFromApi
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isNotDefined
import com.cjbooms.fabrikt.util.YamlUtils
import com.cjbooms.fabrikt.validation.ValidationError
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import java.nio.file.Path
import java.nio.file.Paths

data class SourceApi(
    val rawApiSpec: String,
    val baseDir: Path = Paths.get("").toAbsolutePath()
) {
    companion object {
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
    val modelInfos: List<ModelInfo>

    init {
        validateSchemaObjects(openApi3).let {
            if (it.isNotEmpty()) throw ParameterException("Invalid models or api file:\n${it.joinToString("\n\t")}")
        }
        modelInfos = modelInfosFromApi(openApi3)
    }

    private fun validateSchemaObjects(api: OpenApi3): List<ValidationError> =
        api.schemas.map { it.value.properties }.flatMap { it.entries }.fold(emptyList()) { lst, entry ->
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
