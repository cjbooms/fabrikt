package views

import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.ResourceFile
import com.cjbooms.fabrikt.model.ResourceSourceSet
import com.cjbooms.fabrikt.model.SimpleFile
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.withCharset
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import views.elements.fileView
import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.stream.appendHTML

fun FlowContent.addFile(file: GeneratedFile) = when (file) {
    is KotlinSourceSet -> {
        div {
            file.files.forEach {
                val stringBuilder = StringBuilder()
                it.writeTo(stringBuilder)
                val code = stringBuilder.toString()
                fileView("// ${it.name}.kt\n$code")
            }
        }
    }
    is SimpleFile -> {
        div {
            fileView("// ${file.path.fileName}\n${file.content}")
        }
    }
    is ResourceFile -> TODO()
    is ResourceSourceSet -> TODO()
}

/**
 * Responds with a div containing the provided block.
 */
suspend fun ApplicationCall.respondHtmlFragmentDiv(status: HttpStatusCode = HttpStatusCode.OK, block: FlowContent.() -> Unit) {
    val text = buildString {
        appendHTML().div { block() }
    }
    respond(TextContent(text, ContentType.Text.Html.withCharset(Charsets.UTF_8), status))
}
