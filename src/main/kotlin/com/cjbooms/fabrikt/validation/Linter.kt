package com.cjbooms.fabrikt.validation

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.RuleSetProvider
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

object Linter {

    private const val KOTLIN_EXTENSION = "kt"
    private const val ARBITRARY_FOLDER_DEPTH = 20
    private val lintRuleSets = ServiceLoader.load(RuleSetProvider::class.java)
        .map { it.get() }.sortedBy {
            when (it.id) {
                "standard" -> 0
                else -> 1
            }
        }

    fun lint(directory: Path) {
        val matcher = FileSystems.getDefault().getPathMatcher("glob:**.$KOTLIN_EXTENSION")
        val ktSourceFiles = Files.find(
            directory, ARBITRARY_FOLDER_DEPTH,
            { file, _ -> matcher.matches(file) }
        )
        ktSourceFiles.forEach { path ->
            val file = path.toFile()
            val fileContent = file.readText()
            val lintedContent = lintString(fileContent)
            if (fileContent != lintedContent) file.writeText(lintedContent)
        }
    }

    fun lintString(rawText: String) =
        // lint twice, first lint adds whitespace after each line in a multiline field annotation.
        internalLintString(internalLintString(rawText))

    private fun internalLintString(rawText: String) = KtLint.format(
        KtLint.Params(
            text = rawText,
            ruleSets = lintRuleSets,
            cb = { _, _ -> }
        )
    )
}
