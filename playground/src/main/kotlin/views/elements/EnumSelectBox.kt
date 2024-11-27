package views.elements

import kotlinx.html.FlowContent
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.label
import kotlinx.html.option
import kotlinx.html.select
import kotlinx.html.unsafe

fun FlowContent.enumSelectBox(name: String, enumValues: Array<out Enum<*>>, default: String = "", emptyOption: Boolean = false) {
    div("mb2") {
        label("block mb1 bold") {
            htmlFor = name
            +name
        }
        select {
            classes = setOf("block", "border", "p2", "rounded", "overflow-hidden")
            this.name = name
            if (emptyOption) {
                option {
                    value = ""
                    +""
                }
            }
            enumValues.forEach { enumValue ->
                option {
                    selected = enumValue.name == default
                    value = enumValue.name
                    unsafe { +formatBackticks(enumValue.toString()) }
                }
            }
        }
    }
}
