package views.layout

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.style

fun FlowContent.columnPanel(flexSizes: List<Double> = listOf(), vararg content: FlowContent.() -> Unit) {
    div {
        style = "display: flex; flex-direction: row; height: 100vh; overflow: hidden; position: fixed; width: 100%;"
        content.forEachIndexed { index, it ->
            div("panel") {
                style = "flex: ${flexSizes.getOrElse(index){ 1 }}; padding: 10px; overflow-y: scroll; overflow-x: hidden;"
                it()
            }
        }
    }
}
