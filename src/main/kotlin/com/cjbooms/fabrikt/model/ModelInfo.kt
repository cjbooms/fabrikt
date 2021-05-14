package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.PropertyInfo.Companion.topLevelProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlineableMapDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSubType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSimpleType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema

data class ModelInfo(
    private val key: String,
    val schema: Schema,
    val api: OpenApi3,
    val children: List<ModelInfo> = emptyList()
) {
    val name = key
    val safeName = schema.safeName()
    var isSimpleType: Boolean = schema.isSimpleType() && !schema.isEnumDefinition()
    var isInlineableMapDefinition: Boolean = schema.isInlineableMapDefinition()
    val isPolymorphicSuperType: Boolean = schema.isPolymorphicSuperType()
    val isPolymorphicSubType: Boolean = schema.isPolymorphicSubType(api)
    val maybeSuperType: ModelInfo? = schema.getSuperType(api)?.let { ModelInfo(it.name, it, api) }
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
