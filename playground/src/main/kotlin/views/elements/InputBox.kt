package views.elements

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.input
import kotlinx.html.label

fun FlowContent.inputBox(name: String, placeholder: String = "", currentValue: String = "") {
    div("mb2") {
        label("block mb1 bold") {
            htmlFor = name
            +name
        }
        input(classes = "block w-100 border p2 rounded") { this.name = name; this.placeholder = placeholder; value = currentValue }
    }
}
