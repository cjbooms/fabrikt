package views.elements

import com.cjbooms.fabrikt.model.GeneratedFile
import com.cjbooms.fabrikt.model.KotlinSourceSet
import com.cjbooms.fabrikt.model.ResourceFile
import com.cjbooms.fabrikt.model.ResourceSourceSet
import com.cjbooms.fabrikt.model.SimpleFile
import kotlinx.html.FlowContent
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.pre

fun FlowContent.fileView(content: String, name: String? = null) {
    div {
        if (name != null) {
            id = name
        }
        pre {
            code(classes = "language-kotlin") {
                +content
            }
        }
    }
}

fun FlowContent.fileViewForFile(file: GeneratedFile) = when (file) {
    is KotlinSourceSet -> {
        div {
            file.files.forEach {
                val stringBuilder = StringBuilder()
                it.writeTo(stringBuilder)
                val code = stringBuilder.toString()
                fileView("// ${it.name}\n$code", it.name)
            }
        }
    }
    is SimpleFile -> {
        div {
            fileView("// ${file.path.fileName}\n${file.content}", file.path.fileName.toString())
        }
    }
    is ResourceFile -> {
        div {
            val code = file.inputStream.bufferedReader().use { reader -> reader.readText() }
            fileView("// ${file.fileName}\n$code", file.fileName)
        }
    }
    is ResourceSourceSet -> {
        div {
            file.files.forEach {
                val code = it.inputStream.bufferedReader().use { reader -> reader.readText() }
                fileView("// ${it.fileName}\n$code", it.fileName)
            }
        }
    }
}
