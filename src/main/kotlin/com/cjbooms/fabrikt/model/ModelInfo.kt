package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.PropertyInfo.Companion.topLevelProperties
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getPolymorphicSubTypes
import com.cjbooms.fabrikt.util.KaizenParserExtensions.getSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isEnumDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInLinedObjectUnderAllOf
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isInlineableMapDefinition
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSubType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isPolymorphicSuperType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.isSimpleType
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import com.reprezen.kaizen.oasparser.model3.Schema

data class ModelInfo(
    private val key: String,
    val schema: Schema,
    val api: OpenApi3,
    val children: List<ModelInfo> = emptyList()
) {
    private val parentToChildren = parentToChildren(api)
    val name = key
    val kotlinModelName = name.toModelClassName()
    val safeName = schema.safeName()
    val maybeDiscriminator = schema.discriminator?.propertyName
    var isSimpleType: Boolean = schema.isSimpleType() && !schema.isEnumDefinition()
    var isInlineableMapDefinition: Boolean = schema.isInlineableMapDefinition()
    val isPolymorphicSuperType: Boolean = schema.isPolymorphicSuperType()
    val polymorphicSubTypes = schema.getPolymorphicSubTypes(api)
    val isPolymorphicSubType: Boolean = schema.isPolymorphicSubType(api)
    val maybeSuperType: ModelInfo? = schema.getSuperType(api)?.let { ModelInfo(it.name, it, api) }
    val childSchemas: Collection<String> = parentToChildren[key].orEmpty()
    val typeInfo: KotlinTypeInfo = KotlinTypeInfo.from(schema, name)
    val properties: Collection<PropertyInfo> = schema.topLevelProperties(PropertyInfo.HTTP_SETTINGS, schema)

    @Suppress("UNCHECKED_CAST")
    companion object {

        internal fun modelInfosFromApi(api: OpenApi3): List<ModelInfo> {
            return api.schemas.entries.map { it.key to it.value }
                .plus(api.parameters.entries.map { it.key to it.value.schema })
                .map { (key, schema) -> ModelInfo(key, schema, api) }
        }

        private fun parentToChildren(api: OpenApi3): Map<String, List<String>> {
            val listOfParentChildMaps = api.schemas
                .map { (_, schema: Schema) ->
                    schema
                        .allOfSchemas
                        .filterNot { it.isInLinedObjectUnderAllOf() }
                        .map { it.safeName() to schema }
                        .toMap()
                }
                .filter { it.isNotEmpty() }
            return listOfParentChildMaps
                .fold(mapOf<String, List<String>>()) { acc, parentChildMap ->
                    parentChildMap
                        .map { (key, schema) ->
                            acc.plus(key to (acc[key]?.let { it + schema.safeName() }
                                ?: listOf(schema.safeName())))
                        }
                        .reduceOrNull { a, b -> a.plus(b) } ?: acc
                }
                .toMap()
        }

        private fun <S, T : S> List<T>.reduceOrNull(op: (acc: S, T) -> S): S? {
            val iterator = this.iterator()
            return if (!iterator.hasNext()) null
            else this.reduce { acc: S, t: T -> op(acc, t) }
        }
    }
}
