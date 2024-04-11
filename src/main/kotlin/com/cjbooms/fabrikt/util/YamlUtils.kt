package com.cjbooms.fabrikt.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
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
                .loaderOptions(
                    LoaderOptions().apply {
                        codePointLimit = 100 * 1024 * 1024 // 100MB }
                    }
                )
                .build()
        )
            .registerKotlinModule()
            .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
    private val internalMapper: ObjectMapper =
        ObjectMapper(YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER))

    private val NULL_TYPE: JsonNode = objectMapper.valueToTree("null")

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
            if (root["openapi"].asText() == "3.1.0") {
                downgradeNullableSyntax(root)
            }
            OpenApi3Parser().parse(root, inputDir.toUri().toURL())
        } catch (ex: NullPointerException) {
            throw IllegalArgumentException(
                "The Kaizen openapi-parser library threw a NPE exception when parsing this API. " +
                    "This is commonly due to an external schema reference that is unresolvable, " +
                    "possibly due to a lack of internet connection",
                ex,
            )
        }

    private fun downgradeNullableSyntax(node: JsonNode) {
        when {
            node.isObject -> {
                var requiresNullable = false
                node.fields().forEach { (key, maybeTypeArray) ->
                    if (key == "type" && maybeTypeArray.isArray && maybeTypeArray.contains(NULL_TYPE)) {
                        val nonNullType = maybeTypeArray.first { it != NULL_TYPE }
                        (node as ObjectNode).replace("type", nonNullType)
                        requiresNullable = true
                    }
                    downgradeNullableSyntax(maybeTypeArray)
                }
                if (requiresNullable) {
                    (node as ObjectNode).put("nullable", true)
                }
            }

            node.isArray -> {
                node.forEach { downgradeNullableSyntax(it) }
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
