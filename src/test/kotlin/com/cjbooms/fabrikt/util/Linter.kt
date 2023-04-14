package com.cjbooms.fabrikt.util

import com.pinterest.ktlint.core.Code
import com.pinterest.ktlint.core.KtLintRuleEngine
import com.pinterest.ktlint.ruleset.standard.StandardRuleSetProvider

object Linter {

    fun lintString(rawText: String): String {
        val code = Code.CodeSnippet(rawText)
        val result = KtLintRuleEngine(
            ruleProviders = StandardRuleSetProvider().getRuleProviders(),
        ).format(code)
        return result
    }
}
