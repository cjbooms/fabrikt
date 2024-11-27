package views.elements

import kotlinx.html.FlowContent
import kotlinx.html.code
import kotlinx.html.pre

fun FlowContent.fileView(content: String) {
    pre {
        code(classes = "language-kotlin") {
            +content
        }
    }
}