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
import data.sampleOpenApiSpec
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.stream.appendHTML
import kotlinx.html.style
import kotlinx.html.unsafe
import lib.generateCodeSynchronized
import views.addFile
import views.elements.codeView
import views.elements.fileView
import views.elements.specForm
import views.layout.columnPanel
import views.layout.mainLayout
import views.respondHtmlFragmentDiv

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull() ?: 8080) {
        routing {
            staticResources("/static", "static")

            /**
             * GET endpoint to render the playground
             */
            get("/") {
                call.respondHtml {
                    mainLayout {
                        columnPanel(
                            flexSizes = listOf(1.0, 1.0, 0.5),
                            // first column
                            {
                                specForm(sampleOpenApiSpec)
                            },
                            // second column
                            {
                                codeView { fileView("// Output will appear here") }
                            },
                            // third column
                            {
                                h3 {
                                    style = "margin-top: 0;"
                                    +"Happy with what you see?"
                                }
                                p {
                                    +"Embed Fabrikt in your project and start generating code from OpenAPI specs today!"
                                }
                                p {
                                    a(href = "https://github.com/cjbooms/fabrikt", target = "_blank") {
                                        +"Fabrikt on GitHub"
                                    }
                                }
                            }
                        )
                    }
                }
            }

            /**
             * POST endpoint to generate code from a spec
             *
             * Renders only the div containing the generated code.
             *
             * Loaded via AJAX with HTMX.
             */
            post("/generate") {
                val body = call.receiveParameters()

                // validate input
                val inputSpec = body["spec"]
                if (inputSpec.isNullOrBlank()) {
                    return@post call.respondText {
                        buildString { appendHTML().div {
                            fileView("// Error: No spec provided")
                            script { unsafe { +"Prism.highlightAll();" } } // trigger syntax highlighting
                        } }
                    }
                }

                // parse input
                val serializationLibraryInput = body["serializationLibrary"]
                val serializationLibrary: SerializationLibrary? = if (serializationLibraryInput != null) {
                    SerializationLibrary.valueOf(serializationLibraryInput)
                } else null

                val genTypes: Set<CodeGenerationType> =
                    body.getAll("genTypes")?.map { CodeGenerationType.valueOf(it) }?.toSet()
                        ?: emptySet()

                val modelOptions: Set<ModelCodeGenOptionType> =
                    body.getAll("modelOptions")?.map { ModelCodeGenOptionType.valueOf(it) }?.toSet() ?: emptySet()

                val controllerTargetInput = body["controllerTarget"]
                val controllerTarget: ControllerCodeGenTargetType? = if (!controllerTargetInput.isNullOrBlank()) {
                    ControllerCodeGenTargetType.valueOf(controllerTargetInput)
                } else null

                val controllerOptions = body.getAll("controllerOptions")?.map{ ControllerCodeGenOptionType.valueOf(it) }?.toSet()
                    ?: emptySet()

                val modelSuffix = body["modelSuffix"]

                val clientOptions = body.getAll("clientOptions")?.map { ClientCodeGenOptionType.valueOf(it) }?.toSet()
                    ?: emptySet()

                val clientTargetInput = body["clientTarget"]
                val clientTarget = if (!clientTargetInput.isNullOrBlank()) {
                    ClientCodeGenTargetType.valueOf(clientTargetInput)
                } else null

                val typeOverrides = body.getAll("typeOverrides")?.map { CodeGenTypeOverride.valueOf(it) }?.toSet()
                    ?: emptySet()

                val validationLibraryInput = body["validationLibrary"]
                val validationLibrary = if (!validationLibraryInput.isNullOrBlank()) {
                    ValidationLibrary.valueOf(validationLibraryInput)
                } else null

                val externalRefResolutionModeInput = body["externalRefResolutionMode"]
                val externalRefResolutionMode = if (!externalRefResolutionModeInput.isNullOrBlank()) {
                    ExternalReferencesResolutionMode.valueOf(externalRefResolutionModeInput)
                } else null

                runCatching {
                    generateCodeSynchronized(
                        genTypes,
                        serializationLibrary,
                        modelOptions,
                        controllerTarget,
                        inputSpec,
                        controllerOptions,
                        modelSuffix,
                        clientOptions,
                        clientTarget,
                        typeOverrides,
                        validationLibrary,
                        externalRefResolutionMode,
                    )
                }.onSuccess { generatedFiles ->
                    call.respondHtmlFragmentDiv {
                        if (generatedFiles.isEmpty()) {
                            fileView("// No files generated. Try adjusting your settings.")
                        } else {
                            generatedFiles.forEach {
                                addFile(it)
                            }
                        }
                        script { unsafe { +"Prism.highlightAll();" } } // trigger syntax highlighting
                    }
                }.onFailure { error ->
                    call.respondHtmlFragmentDiv {
                            fileView("// Error: ${error.message}")
                            script { unsafe { +"Prism.highlightAll();" } } // trigger syntax highlighting
                    }
                }
            }
        }
    }.start(wait = true)
}
