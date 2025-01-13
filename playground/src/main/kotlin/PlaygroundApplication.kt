import com.cjbooms.fabrikt.cli.CodeGenerationType
import com.cjbooms.fabrikt.cli.ModelCodeGenOptionType
import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.ResourceFile
import com.cjbooms.fabrikt.model.ResourceSourceSet
import com.cjbooms.fabrikt.model.SimpleFile
import data.sampleOpenApiSpec
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.html.respondHtml
import io.ktor.server.http.content.staticResources
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
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
import lib.GenerationSettings.Companion.receiveGenerationSettings
import views.elements.fileViewForFile
import views.elements.codeView
import views.elements.fileView
import views.elements.specForm
import views.layout.columnPanel
import views.layout.mainLayout
import views.respondHtmlFragmentDiv

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toIntOrNull() ?: 8080) {
        install(CallLogging)

        routing {
            staticResources("/static", "static")

            /**
             * GET endpoint to render the playground
             */
            get("/") {
                val generationSettings = call.queryParameters.receiveGenerationSettings()
                    .copy(inputSpec = sampleOpenApiSpec) // set the sample spec
                    .run {
                        // if no settings in query params we configure default
                        // to ensure something is generated with just the sample spec
                        if (call.queryParameters.isEmpty()) {
                            copy(genTypes = setOf(CodeGenerationType.HTTP_MODELS))
                        } else {
                            this
                        }
                    }

                call.respondHtml {
                    mainLayout {
                        columnPanel(
                            flexSizes = listOf(1.0, 1.0, 0.5),
                            // first column
                            {
                                specForm(generationSettings)
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
                val generationSettings = call.receiveParameters().receiveGenerationSettings()

                // validate input
                val inputSpec = generationSettings.inputSpec
                if (inputSpec.isBlank()) {
                    return@post call.respondText {
                        buildString { appendHTML().div {
                            fileView("// Error: No spec provided")
                            script { unsafe { +"Prism.highlightAll();" } } // trigger syntax highlighting
                        } }
                    }
                }

                runCatching {
                    generateCodeSynchronized(generationSettings)
                }.onSuccess { generatedFiles ->
                    val pathParams: String = generationSettings.toQueryParams()
                    call.response.header("HX-Replace-Url", "/?$pathParams")

                    val fileNames = generatedFiles.fileNames()

                    call.respondHtmlFragmentDiv {
                        if (generatedFiles.isEmpty()) {
                            fileView("// No files generated. Try adjusting your settings.")
                        } else {
                            fileList(fileNames)
                            generatedFiles.forEach {
                                fileViewForFile(it)
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

private fun List<GeneratedFile>.fileNames(): List<String> = this.map { generatedFile ->
    when (generatedFile) {
        is KotlinSourceSet -> generatedFile.files.map { it.name }
        is SimpleFile -> listOf(generatedFile.path.fileName.toString())
        is ResourceFile -> listOf(generatedFile.fileName)
        is ResourceSourceSet -> generatedFile.files.map { it.fileName }
    }
}.flatten()
