package views.elements

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.id

fun FlowContent.codeView(content: FlowContent.() -> Unit) {
    div {
        id = "codeview"
        content()
    }
}
