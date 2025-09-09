package com.cjbooms.fabrikt.util

import com.pinterest.ktlint.rule.engine.api.Code
import com.pinterest.ktlint.rule.engine.api.KtLintRuleEngine
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider

object Linter {

    val shouldLint = true
    fun lintString(rawText: String): String {
        return if (shouldLint) {
            val code = Code.fromSnippet(rawText)
            val result = KtLintRuleEngine(
                ruleProviders = StandardRuleSetProvider().getRuleProviders(),
            ).format(code)
            result
        } else rawText
    }
}
