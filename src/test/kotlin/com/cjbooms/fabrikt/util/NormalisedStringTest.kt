package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.util.NormalisedString.camelCase
import com.cjbooms.fabrikt.util.NormalisedString.isValidJavaPackage
import com.cjbooms.fabrikt.util.NormalisedString.pascalCase
import com.cjbooms.fabrikt.util.NormalisedString.toEnumName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NormalisedStringTest {

    @Test
    fun `should transform string with underscores to pascal case`() {
        assertThat("abc_def-hij".pascalCase()).isEqualTo("AbcDefHij")
    }
    @Test
    fun `should handle norwegian characters`() {
        assertThat("Åabc_øæå-ØÆÅ-hij".pascalCase()).isEqualTo("ÅabcØæåØÆÅHij")
    }

    @Test
    fun `should transform string with underscores to camel case`() {
        assertThat("abc_def".camelCase()).isEqualTo("abcDef")
    }

    @Test
    fun `should transform string with other random characters to pascal case`() {
        assertThat("abc%def3g".camelCase()).isEqualTo("abcDef3g")
    }

    @Test
    fun `isValidJavaPackage should return false on a hyphenated-name`() {
        assertThat("hyphenated-name".isValidJavaPackage()).isFalse
    }

    @Test
    fun `isValidJavaPackage should return false when a section begins with a digit`() {
        assertThat("123name".isValidJavaPackage()).isFalse
    }

    @Test
    fun `isValidJavaPackage should return false when there is an upper-case letter present`() {
        assertThat("myPackage".isValidJavaPackage()).isFalse
    }

    @Test
    fun `isValidJavaPackage should return true for a simple valid package`() {
        assertThat("com.cjbooms.fabrikt".isValidJavaPackage()).isTrue
    }

    @Test
    fun `toEnumName should return an upper snake case enum with no special characters`() {
        assertThat("PascalCase_enumWith-special/characters.json".toEnumName())
            .isEqualTo("PASCAL_CASE_ENUM_WITH_SPECIAL_CHARACTERS_JSON")
    }
    @Test
    fun `toEnumName should return an upper snake case enum with with norwegian characters`() {
        assertThat("PascalCase_enumWith-ÅabcØæ-åØÆÅHij-special/characters.json".toEnumName())
            .isEqualTo("PASCALCASE_ENUMWITH_ÅABCØÆ_ÅØÆÅHIJ_SPECIAL_CHARACTERS_JSO")
    }
}
