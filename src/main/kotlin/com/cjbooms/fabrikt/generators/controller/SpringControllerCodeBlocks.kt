package com.cjbooms.fabrikt.generators.controller

import com.cjbooms.fabrikt.generators.controller.metadata.SpringImports
import com.cjbooms.fabrikt.model.IncomingParameter
import com.squareup.kotlinpoet.CodeBlock

data class SimpleCodeBlock(val body: String, val args: List<Any> = emptyList())

fun SimpleCodeBlock.toCodeBlock() = CodeBlock.of(body, *args.toTypedArray())

object ControllerCodeBlocks {

    fun postWithResponsebody(serviceFuncName: String, parameters: List<IncomingParameter>): SimpleCodeBlock =
        SimpleCodeBlock(
            """return ResponseEntity
                .ok(${serviceCallLine(serviceFuncName, parameters)}.second!!)
            """
        )

    fun postWithLocationResponse(serviceFuncName: String, parameters: List<IncomingParameter>, withBody: Boolean): SimpleCodeBlock {
        val conditionalObjectResponse = "if (svcResp.second != null) response.body(svcResp.second)"
        return SimpleCodeBlock(
            """val svcResp = ${serviceCallLine(serviceFuncName, parameters)}
                val response = %T.created(svcResp.first) ${if (withBody) "\n$conditionalObjectResponse" else "" }
                return response.build()
               """.trimIndent(),
            listOf(
                SpringImports.RESPONSE_ENTITY
            )
        )
    }

    fun getSingleResource(serviceFuncName: String, parameters: List<IncomingParameter>): SimpleCodeBlock =
        SimpleCodeBlock(
            """val svcResp = ${serviceCallLine(serviceFuncName, parameters)}
               return %T
                   .ok()
                   .body(svcResp)
               """.trimIndent(),
            listOf(
                SpringImports.RESPONSE_ENTITY
            )
        )

    fun putWithNoContent(serviceFuncName: String, parameters: List<IncomingParameter>): SimpleCodeBlock {
        return SimpleCodeBlock(
            """${serviceCallLine(serviceFuncName, parameters)}
               return ResponseEntity.noContent().build()
            """
        )
    }

    fun deleteWithNoContent(serviceFuncName: String, parameters: List<IncomingParameter>): SimpleCodeBlock {
        return SimpleCodeBlock(
            """${serviceCallLine(serviceFuncName, parameters)}
               return ResponseEntity.noContent().build()
            """
        )
    }

    fun genericBody(serviceFuncName: String, parameters: List<IncomingParameter>, responseCode: Int): SimpleCodeBlock {
        return SimpleCodeBlock(
            """${serviceCallLine(serviceFuncName, parameters)}
            return ResponseEntity.status($responseCode).build()
            """, listOf(SpringImports.URI_BUILDER)
        )
    }

    private fun serviceCallLine(serviceFuncName: String, parameters: List<IncomingParameter>): String =
        "service.$serviceFuncName(${parameters.joinToString(", ") { it.name }})"
}
