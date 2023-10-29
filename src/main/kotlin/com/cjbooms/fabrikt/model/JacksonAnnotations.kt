package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.generators.model.JacksonMetadata
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.PropertySpec

object JacksonAnnotations : Annotations {
    override fun addIgnore(property: PropertySpec.Builder) {
        property.addAnnotation(JacksonMetadata.ignore)
    }

    override fun addGetter(`fun`: FunSpec.Builder): FunSpec.Builder {
        return `fun`.addAnnotation(JacksonMetadata.anyGetter)
    }

    override fun addSetter(`fun`: FunSpec.Builder): FunSpec.Builder {
        return `fun`.addAnnotation(JacksonMetadata.anySetter)
    }

    override fun addProperty(property: PropertySpec.Builder, oasKey: String): PropertySpec.Builder {
        return property.addAnnotation(JacksonMetadata.jacksonPropertyAnnotation(oasKey))
    }

    override fun addParameter(
        property: PropertySpec.Builder,
        oasKey: String
    ): PropertySpec.Builder {
        return property.addAnnotation(JacksonMetadata.jacksonParameterAnnotation(oasKey))
    }
}