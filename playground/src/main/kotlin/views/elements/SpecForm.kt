package views.elements

import com.cjbooms.fabrikt.cli.ClientCodeGenOptionType
import com.cjbooms.fabrikt.cli.ClientCodeGenTargetType
import com.cjbooms.fabrikt.cli.CodeGenTypeOverride
import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ControllerCodeGenOptionType
import com.cjbooms.fabrikt.cli.ControllerCodeGenTargetType
import com.cjbooms.fabrikt.cli.ExternalReferencesResolutionMode
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.cli.SerializationLibrary
import com.cjbooms.fabrikt.cli.ValidationLibrary
import kotlinx.html.FlowContent
import kotlinx.html.FormMethod
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.hidden
import kotlinx.html.id
import kotlinx.html.script
import kotlinx.html.style
import kotlinx.html.submitInput
import kotlinx.html.textArea
import kotlinx.html.unsafe

fun FlowContent.specForm(prefill: String? = "") = div {
    // editor (used by Ace)
    div {
        id = "editor" + ""
        style = "height: 400px; width: 100%;"
    }

    form(action = "/generate", method = FormMethod.post) {
        // backing field for editor
        textArea {
            id = "spec"
            name = "spec"
            hidden = true
            +"$prefill"
        }

        // submit button
        submitInput(classes = "mt2 rounded border-none py2 max-width-3 btn-generate") {
            value = "Generate!"
            // htmx setup
            attributes["hx-post"] = "/generate"
            attributes["hx-target"] = "#codeview"
        }

        // configuration options
        div("h3 mt3 mb1") { +"What to generate" }
        enumCheckboxes("genTypes", CodeGenerationType.values(), setOf(CodeGenerationType.HTTP_MODELS.name))

        div("h3 mt3 mb1") { +"Model Options" }
        enumSelectBox("serializationLibrary", SerializationLibrary.values(), SerializationLibrary.JACKSON.name)
        enumCheckboxes("modelOptions", ModelCodeGenOptionType.values())
        inputBox("modelSuffix", "Dto")
        enumCheckboxes("typeOverrides", CodeGenTypeOverride.values())

        div("h3 mt3 mb1") { +"Client Options" }
        enumSelectBox("clientTarget", ClientCodeGenTargetType.values(), ClientCodeGenTargetType.OK_HTTP.name)
        enumCheckboxes("clientOptions", ClientCodeGenOptionType.values())

        div("h3 mt3 mb1") { +"Server Options" }
        enumSelectBox("controllerTarget", ControllerCodeGenTargetType.values(), ControllerCodeGenTargetType.SPRING.name)
        enumCheckboxes("controllerOptions", ControllerCodeGenOptionType.values())

        div("h3 mt3 mb1") { +"Validation Options" }
        enumSelectBox("validationLibrary", ValidationLibrary.values(), ValidationLibrary.JAVAX_VALIDATION.name)

        div("h3 mt3 mb1") { +"External References" }
        enumSelectBox("externalRefResolutionMode", ExternalReferencesResolutionMode.values(), ExternalReferencesResolutionMode.TARGETED.name)
    }

    // enable Ace editor and connect with backing field
    script {
        unsafe {
            +"""
                // Initialize Ace editor
                var editor = ace.edit("editor");
                var textarea = document.getElementById("spec");
                editor.getSession().setValue(textarea.value);
                editor.getSession().setMode("ace/mode/yaml");
                
                // keep the text area in sync with the editor content
                editor.getSession().on('change', function(){
                    textarea.value = editor.getSession().getValue();
                });
            """.trimIndent()
        }
    }
}

fun formatBackticks(input: String): String {
    val regex = Regex("`([^`]+)`")
    return input.replace(regex) { matchResult ->
        val codeText = matchResult.groupValues[1]
        codeText
    }
}
