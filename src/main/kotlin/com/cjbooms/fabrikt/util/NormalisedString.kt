package com.cjbooms.fabrikt.util

import javax.lang.model.SourceVersion

object NormalisedString {

    fun String.pascalCase(): String =
        replaceSpecialCharacters()
            .split("_")
            .joinToString("") { it.capitalize() }

    private fun String.replaceSpecialCharacters() =
        Regex("[^A-Za-z0-9]").replace(this, "_")

    private fun String.camelToSnake() =
        Regex("[a-z][A-Z]")
            .replace(this) { pair ->
                val characters = pair.value.toCharArray()
                "${characters[0]}_${characters[1]}"
            }

    fun String.camelCase(): String = this.pascalCase().decapitalize()

    fun String.toModelClassName(parentModelName: String = ""): String = parentModelName + this.pascalCase()

    fun String.toMapValueClassName(): String = "${this.pascalCase()}Value"

    fun String.toEnumName(): String =
        replaceSpecialCharacters()
            .camelToSnake()
            .toUpperCase()

    fun String.toKotlinParameterName(): String = this.camelCase()

    fun String.isValidJavaPackage(): Boolean {
        val pieces = split(".")
        return all { if (it.isLetter()) it.isLowerCase() else true } && pieces.all { SourceVersion.isIdentifier(it) }
    }
}
