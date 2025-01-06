package views

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.stream.appendHTML

/**
 * Responds with a div containing the provided block.
 */
suspend fun ApplicationCall.respondHtmlFragmentDiv(status: HttpStatusCode = HttpStatusCode.OK, block: FlowContent.() -> Unit) {
    val text = buildString {
        appendHTML().div { block() }
    }
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}
