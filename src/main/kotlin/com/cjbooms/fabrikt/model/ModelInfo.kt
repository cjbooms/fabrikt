package com.cjbooms.fabrikt.model

import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema

data class ModelInfo(
    val name: String,
    val schema: Schema,
    val api: OpenApi3,
) {
    val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, name)

    @Suppress("UNCHECKED_CAST")
    companion object {

        internal fun modelInfosFromApi(api: OpenApi3): List<ModelInfo> {
            return api.schemas.entries.map { it.key to it.value }
                .plus(api.parameters.entries.map { it.key to it.value.schema })
                .map { (key, schema) -> ModelInfo(key, schema, api) }
        }
    }
}
