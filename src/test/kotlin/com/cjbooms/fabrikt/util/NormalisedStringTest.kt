package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.util.NormalisedString.abbreviate
import com.cjbooms.fabrikt.util.NormalisedString.camelCase
import com.cjbooms.fabrikt.util.NormalisedString.camelToSnakeCase
import com.cjbooms.fabrikt.util.NormalisedString.isValidJavaPackage
import com.cjbooms.fabrikt.util.NormalisedString.pascalCase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class NormalisedStringTest {

    @Test
    fun `should transform string with underscores to pascal case`() {
        assertThat("abc_def-hij".pascalCase()).isEqualTo("AbcDefHij")
    }

    @Test
    fun `should transform string with underscores to camel case`() {
        assertThat("abc_def".camelCase()).isEqualTo("abcDef")
    }

    @Test
    fun `should transform string with other random characters to pascal case`() {
        assertThat("abc%def3g".camelCase()).isEqualTo("abcDefG")
    }

    @Test
    fun `should convert from camel to snake case`() {
        assertThat("camelCase".camelToSnakeCase()).isEqualTo("camel_case")
    }

    @Test
    fun `isValidJavaPackage should return false on a hyphenated-name`() {
        assertThat("hyphenated-name".isValidJavaPackage()).isFalse()
    }

    @Test
    fun `isValidJavaPackage should return false when a section begins with a digit`() {
        assertThat("123name".isValidJavaPackage()).isFalse()
    }

    @Test
    fun `isValidJavaPackage should return false when there is an upper-case letter present`() {
        assertThat("myPackage".isValidJavaPackage()).isFalse()
    }

    @Test
    fun `isValidJavaPackage should return true for a simple valid package`() {
        assertThat("com.cjbooms.fabrikt".isValidJavaPackage()).isTrue()
    }

    @Test
    fun `should abbreviate pascal cased string `() {
        assertThat("GrandParentObjectParentObject".abbreviate()).isEqualTo("GrPaObPaOb")
    }
}
