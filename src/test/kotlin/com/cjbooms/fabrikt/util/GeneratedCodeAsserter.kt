package com.cjbooms.fabrikt.util

import com.cjbooms.fabrikt.util.GeneratedCodeAsserter.Companion.SHOULD_OVERWRITE_EXAMPLES
import com.cjbooms.fabrikt.util.ResourceHelper.readTextResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.io.path.Path as KPath

class GeneratedCodeAsserter(val generatedCode: String) {

    companion object {
        // Set this to true to overwrite the expected files with the generated code when they don't match
        const val SHOULD_OVERWRITE_EXAMPLES = false

        fun assertThatGenerated(generatedCode: String): GeneratedCodeAsserter = GeneratedCodeAsserter(generatedCode)
    }

    /**
     * Asserts that the generated code is equal to the content of the resource file at the given path.
     * @param resourcePath The path to the resource file to compare against.
     */
    fun isEqualTo(resourcePath: String) {
        val expectedText = readTextResource(resourcePath)
        try {
            assertThat(generatedCode).isEqualTo(expectedText)
        } catch (ex: AssertionError) {
            if (SHOULD_OVERWRITE_EXAMPLES) {
                println("Mismatch found. Attempting to fix the source file.")
                val sourceFilePath: Path = KPath("src", "test", "resources", resourcePath)
                println("Overwriting existing file: $sourceFilePath")
                sourceFilePath.writeText(generatedCode)
            }
            throw ex
        }
    }
}

class OverWriteProtectionTest {
    @Test
    fun `should fail if the overwrite files is set to true to prevent accidental commit`() {
        assertThat(SHOULD_OVERWRITE_EXAMPLES).isFalse()
    }
}

