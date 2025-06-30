package com.cjbooms.fabrikt.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLFactoryBuilder
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.reprezen.kaizen.oasparser.OpenApi3Parser
import com.reprezen.kaizen.oasparser.model3.OpenApi3
import org.yaml.snakeyaml.LoaderOptions
import java.nio.file.Path
import java.nio.file.Paths

object YamlUtils {

    val objectMapper: ObjectMapper =
        ObjectMapper(
            YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .increaseMaxFileSize()
                .build()
        )
            .registerKotlinModule()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    private val internalMapper: ObjectMapper =
        ObjectMapper(
            YAMLFactory.builder()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .increaseMaxFileSize()
                .build()
        )

    private val NULL_TYPE: JsonNode = objectMapper.valueToTree("null")

    private fun YAMLFactoryBuilder.increaseMaxFileSize(): YAMLFactoryBuilder = loaderOptions(
        LoaderOptions().apply {
            codePointLimit = 100 * 1024 * 1024 // 100MB
        }
    )

    fun mergeYamlTrees(mainTree: String, updateTree: String) =
        internalMapper.writeValueAsString(
            mergeNodes(
                internalMapper.readTree(mainTree),
                internalMapper.readTree(updateTree),
            ),
        )!!

    fun parseOpenApi(input: String, inputDir: Path = Paths.get("").toAbsolutePath()): OpenApi3 =
        try {
            val root: JsonNode = objectMapper.readTree(input)
            val openapiVersion = root["openapi"]?.asText() ?: ""
            if (openapiVersion.startsWith("3.1.")) {
                downgradeNullableSyntax(root)
            }
            cleanEmptyTypes(root)
            OpenApi3Parser().parse(root, inputDir.toUri().toURL())
        } catch (ex: NullPointerException) {
            throw IllegalArgumentException(
                "The Kaizen openapi-parser library threw a NPE exception when parsing this API. " +
                    "This is commonly due to an external schema reference that is unresolvable, " +
                    "possibly due to a lack of internet connection",
                ex,
            )
        }

    fun cleanEmptyTypes(node: JsonNode) {
        when {
            node.isObject -> {
                val objectNode = node as ObjectNode
                val fieldsToProcess = objectNode.fields().asSequence().toList()

                for ((key, value) in fieldsToProcess) {
                    if (key == "type" && (value.isNull || (value.isTextual && value.asText().isBlank()))) {
                        objectNode.remove("type")
                    } else {
                        cleanEmptyTypes(value)
                    }
                }
            }

            node.isArray -> {
                node.forEach { cleanEmptyTypes(it) }
            }
        }
    }

    private fun downgradeNullableSyntax(
        node: JsonNode,
        propertyName: String? = null,
        parentSchemaObject: ObjectNode? = null
    ) {
        // Track the schema object through recursion. Top level attributes, e.g. 'required', may be modified.
        var schemaObject = parentSchemaObject
        if (node.has("properties")) {
            schemaObject = node as ObjectNode
        }
        when {
            node.isObject -> {
                val objectNode = node as ObjectNode
                val fieldsToProcess = objectNode.fields().asSequence().toList()
                var requiresNullable = false

                for ((key, value) in fieldsToProcess) {
                    // Handle `type: ["object", "null"]`
                    if (key == "type" && value.isArray && value.contains(NULL_TYPE)) {
                        val nonNullType = value.first { it != NULL_TYPE }
                        objectNode.replace("type", nonNullType)
                        requiresNullable = true
                    }

                    // Handle `anyOf` or `oneOf` with a `type: null` entry
                    if ((key == "oneOf" || key == "anyOf")
                        && value.isArray
                        && value.size() == 2
                        && value.any { it.isObject && it.has("type") && it.get("type") == NULL_TYPE }
                    ) {
                        val nonNullOption = value.first { !it.has("type") || it.get("type") != NULL_TYPE }

                        if (nonNullOption.isObject) {
                            // Replace the *entire object* with the non-null option, and add nullable
                            objectNode.removeAll()
                            objectNode.setAll<ObjectNode>(nonNullOption.deepCopy<ObjectNode>())
                            objectNode.put("nullable", true)

                            // In OAS 3.0, the 'nullable' sibling property will be ignored for '$ref' types, but the
                            // generated Kotlin type still needs to be recognized as nullable. Without modifying the
                            // referenced schema object or building a new nullable version of that object, we can remove
                            // the field from 'required', which has the same effect on the generated code.
                            if (objectNode.has("\$ref")) {
                                val requiredProperties = schemaObject?.get("required") as? ArrayNode
                                val newRequiredProperties = requiredProperties?.filter { it.textValue() != propertyName }
                                if (newRequiredProperties.isNullOrEmpty()) {
                                    schemaObject?.remove("required")
                                } else {
                                    schemaObject?.replace("required", objectMapper.valueToTree(newRequiredProperties))
                                }
                            }
                            return
                        }
                    }

                    downgradeNullableSyntax(value, key, schemaObject)
                }

                if (requiresNullable) {
                    objectNode.put("nullable", true)
                }
            }

            node.isArray -> {
                node.forEach { downgradeNullableSyntax(it, propertyName, schemaObject) }
            }
        }
    }

    /**
     * The below merge function has been shamelessly stolen from Stackoverflow: https://stackoverflow.com/a/32447591/1026785
     * and converted to much nicer Kotlin implementation
     */
    private fun mergeNodes(currentTree: JsonNode, incomingTree: JsonNode): JsonNode {
        incomingTree.fieldNames().forEach { fieldName ->
            val currentNode = currentTree.get(fieldName)
            val incomingNode = incomingTree.get(fieldName)
            if (currentNode is ArrayNode && incomingNode is ArrayNode) {
                incomingNode.forEach {
                    if (currentNode.contains(it)) {
                        mergeNodes(
                            currentNode.get(currentNode.indexOf(it)),
                            it,
                        )
                    } else {
                        currentNode.add(it)
                    }
                }
            } else if (currentNode is ObjectNode) {
                mergeNodes(currentNode, incomingNode)
            } else {
                (currentTree as? ObjectNode)?.replace(fieldName, incomingNode)
            }
        }
        return currentTree
    }
}
