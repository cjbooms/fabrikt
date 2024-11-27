package views.elements

import kotlinx.html.FlowContent
import kotlinx.html.InputType
import kotlinx.html.classes
import kotlinx.html.div
import kotlinx.html.fieldSet
import kotlinx.html.input
import kotlinx.html.label
import kotlinx.html.legend
import kotlinx.html.span
import kotlinx.html.unsafe

fun FlowContent.enumCheckboxes(name: String, enumValues: Array<out Enum<*>>, defaults: Set<String> = emptySet()) {
    div("mb2") {
        div("block mb1 bold") { +name }
        enumValues.forEach { enumValue ->
            div("mb2") {
                label("flex items-center") {
                    input(type = InputType.checkBox) {
                        classes = setOf("mr1")
                        this.name = name
                        this.value = enumValue.name
                        if (enumValue.name in defaults) {
                            checked = true
                        }
                    }

                    div("regular") { unsafe { +formatBackticks(enumValue.toString()) } }
                }
            }
        }
    }
}
