package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.Destinations.MAIN_KT_SOURCE
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.nio.file.Path

sealed class GeneratedFile {
    abstract fun writeFileTo(outputDir: File)
}

data class SimpleFile(val path: Path, val content: String) : GeneratedFile() {
    override fun writeFileTo(outputDir: File) {
        if (path.parent != null) outputDir.resolve(path.parent.toString()).mkdirs()
        outputDir.resolve(this.path.toString()).writeText(content)
    }
}

data class KotlinSourceSet(val files: Collection<FileSpec>, val srcDir: String = MAIN_KT_SOURCE) : GeneratedFile() {
    override fun writeFileTo(outputDir: File) = files.distinct().forEach { it.writeTo(outputDir.resolve(srcDir)) }
}
