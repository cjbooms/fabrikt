import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.li
import kotlinx.html.style
import kotlinx.html.ul

fun FlowContent.fileList(fileNames: List<String>) {
    ul {
        style = "list-style: none; padding: 0; display: flex; flex-wrap: wrap;"
        fileNames.forEachIndexed { index, fileName ->
            li {
                style = "margin-right: 5px;"
                a(href = "#$fileName") { +fileName }
            }
        }
    }
}
