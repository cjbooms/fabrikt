package com.cjbooms.fabrikt.generators.model

import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.generators.MutableSettings
import com.cjbooms.fabrikt.model.Models
import com.cjbooms.fabrikt.model.QuarkusReflectionModel
import com.cjbooms.fabrikt.model.ResourceFile
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

class QuarkusReflectionModelGenerator(
    private val models: Models,
) {
    private val generationTypes: Set<CodeGenerationType> = MutableSettings.generationTypes
    companion object {
        const val RESOURCE_FILE_NAME = "reflection-config.json"
    }
    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule().enable(SerializationFeature.INDENT_OUTPUT)

    fun generate(): ResourceFile? {
        return if (generationTypes.any { it == CodeGenerationType.QUARKUS_REFLECTION_CONFIG }) {
            val reflectionConfigs = models.models.map {
                QuarkusReflectionModel(it.className.canonicalName)
            }
            ResourceFile(objectMapper.writeValueAsString(reflectionConfigs).byteInputStream(), RESOURCE_FILE_NAME)
        } else null
    }
}
