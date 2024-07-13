package com.cjbooms.fabrikt.models.jackson

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

object Helpers {
    fun mapper(): ObjectMapper {
        val kotlinModule = KotlinModule.Builder().enable(KotlinFeature.NullIsSameAsDefault).build()
        return JsonMapper.builder().addModule(kotlinModule).serializationInclusion(JsonInclude.Include.NON_NULL).build()
    }

}