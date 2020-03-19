package com.cjbooms.fabrikt.util

import javax.lang.model.SourceVersion

object NormalisedString {

    fun String.pascalCase(): String =
        Regex("[^A-Za-z]")
            .replace(this, "_")
            .split("_")
            .joinToString("") { it.capitalize() }

    fun String.camelCase(): String = this.pascalCase().decapitalize()

    fun String.toEntityName(): String = "${this.toModelClassName()}Entity"

    fun String.toModelClassName(): String = this.pascalCase()

    fun String.toMapValueClassName(): String = "${this.pascalCase()}Value"

    fun String.toKotlinParameterName(): String = this.camelCase()

    fun String.toDbTableName(): String = this.removeSuffix("Entity").camelToSnakeCase()

    fun String.toDbFieldName(): String = this.camelToSnakeCase()

    fun String.camelToSnakeCase(): String =
        Regex("[A-Z]").replace(this.camelCase()) {
            "_${it.value.toLowerCase()}"
        }

    fun String.abbreviate() =
        this.camelToSnakeCase().split("_").joinToString("_") { it.take(2) }.pascalCase()

    fun String.isValidJavaPackage(): Boolean {
        val pieces = split(".")
        return all { if (it.isLetter()) it.isLowerCase() else true } && pieces.all { SourceVersion.isIdentifier(it) }
    }
}
