package com.cjbooms.fabrikt.util

import com.pinterest.ktlint.core.KtLint
import com.pinterest.ktlint.core.RuleSetProvider
import java.util.ServiceLoader

object Linter {

    private val lintRuleSets = ServiceLoader.load(RuleSetProvider::class.java)
        .map { it.get() }.sortedBy {
            when (it.id) {
                "standard" -> 0
                else -> 1
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
