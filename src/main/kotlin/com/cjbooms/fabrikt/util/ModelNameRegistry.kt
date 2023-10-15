package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.model.SchemaInfo
import com.cjbooms.fabrikt.util.KaizenParserExtensions.safeName
import com.cjbooms.fabrikt.util.NormalisedString.toModelClassName
import com.reprezen.jsonoverlay.Overlay
import com.reprezen.kaizen.oasparser.model3.Schema
import java.net.URL

/**
 * Model name registry to avoid name collisions
 */
object ModelNameRegistry {
    private val allocatedNames: MutableSet<String> = mutableSetOf()
    private val tagToName: MutableMap<String, String> = mutableMapOf()
    private const val SUFFIX = "Extra"

    /**
     * Registers a new model class name using `schema` and if it is inlined type also based on enclosed schema.
     * The returned value can be queried multiple times by passing `tag` to
     * [ModelNameRegistry.get].
     */
    private fun register(
        schema: Schema,
        enclosingSchema: EnclosingSchema? = null,
        valueClassName: Boolean = false,
        schemaInfoName: String? = null,
    ): String {
        val valueSuffix = if (valueClassName) "Value" else ""
        var suggestion = schema.toModelClassName(schemaInfoName, enclosingSchema?.toModelClassName()) + valueSuffix
        while (allocatedNames.contains(suggestion)) {
            suggestion += SUFFIX
        }
        allocatedNames.add(suggestion)

        val tag = resolveTag(schema, enclosingSchema, valueClassName, schemaInfoName)
        val replaced = tagToName.put(tag, suggestion)
        if (replaced != null) {
            // Only allow unique tags to be registered
            throw IllegalArgumentException("tag $tag cannot be used for both '$replaced' and '$suggestion'")
        }

        return suggestion
    }

    private fun Schema.toModelClassName(schemaInfoName: String? = null, enclosingClassName: String? = null) =
        (enclosingClassName ?: "") + (schemaInfoName?.toModelClassName() ?: safeName().toModelClassName())

    private fun EnclosingSchema.toModelClassName() =
        when (this) {
            is EnclosingSchemaKey -> this.name
            is EnclosingSchemaOasModel -> this.schema.toModelClassName()
            else -> ""
        }

    private fun resolveTag(
        schema: Schema,
        enclosingSchema: EnclosingSchema? = null,
        valueClassName: Boolean = false,
        schemaInfoName: String? = null,
    ): String {
        val overlay = Overlay.of(schema)
        val uri = URL(overlay.jsonReference)
        val typeName = when (true) {
            (schemaInfoName != null) -> schemaInfoName.toModelClassName()
            else -> schema.safeName().toModelClassName()
        }

        var tag = "file:${uri.file}#${enclosingSchema?.toModelClassName() ?: ""}$typeName"
        if (valueClassName) {
            tag += "Value"
        }
        return tag
    }

    /** Retrieve a model class name created with [ModelNameRegistry.register]. */
    operator fun get(tag: String): Result<String> = runCatching {
        requireNotNull(tagToName[tag]) { "unknown tag: $tag" }
    }

    fun getOrRegister(
        schema: Schema,
        enclosingSchema: EnclosingSchema? = null,
        valueClassName: Boolean = false,
    ) = this[resolveTag(schema, enclosingSchema, valueClassName)]
        .getOrElse { register(schema, enclosingSchema, valueClassName) }

    fun getOrRegister(
        schemaInfo: SchemaInfo,
    ) =
        this[resolveTag(schemaInfo.schema, schemaInfoName = schemaInfo.name)]
            .getOrElse { register(schemaInfo.schema, schemaInfoName = schemaInfo.name) }

    fun clear() {
        allocatedNames.clear()
        tagToName.clear()
    }

    enum class EnclosingSchemaType {
        KEY,
        OAS_MODEL,
    }

    /**
     * Enclosing schema can either be a schema from Kaizen as OAS model.
     * Or it can be a root level key in the spec file as captured in an SchemaInfo.
     */
    interface EnclosingSchema {
        val type: EnclosingSchemaType
    }

    data class EnclosingSchemaKey(val name: String) : EnclosingSchema {
        override val type = EnclosingSchemaType.KEY
    }

    data class EnclosingSchemaOasModel(val schema: Schema) : EnclosingSchema {
        override val type = EnclosingSchemaType.OAS_MODEL
    }

    fun Schema.toEnclosingSchemaType() = EnclosingSchemaOasModel(this)

    fun String.toEnclosingSchemaType() = EnclosingSchemaKey(this)
}
