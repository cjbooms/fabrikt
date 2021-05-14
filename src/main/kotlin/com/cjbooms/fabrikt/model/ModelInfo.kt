package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.PropertyInfo.Companion.topLevelProperties
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema

data class ModelInfo(
    private val key: String,
    val schema: Schema,
    val api: OpenApi3,
    val children: List<ModelInfo> = emptyList()
) {
    val name = key
    val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, name)
    val properties: Collection<PropertyInfo> = schema.topLevelProperties(PropertyInfo.HTTP_SETTINGS, schema)

    @Suppress("UNCHECKED_CAST")
    companion object {

        internal fun modelInfosFromApi(api: OpenApi3): List<ModelInfo> {
            return api.schemas.entries.map { it.key to it.value }
                .plus(api.parameters.entries.map { it.key to it.value.schema })
                .map { (key, schema) -> ModelInfo(key, schema, api) }
        }
    }
}
