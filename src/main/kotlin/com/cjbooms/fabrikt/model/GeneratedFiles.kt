package com.cjbooms.fabrikt.model

import com.cjbooms.fabrikt.model.Destinations.MAIN_KT_SOURCE
import com.cjbooms.fabrikt.model.Destinations.MAIN_RESOURCES
import com.cjbooms.fabrikt.util.FileUtils.writeFileTo
import com.squareup.kotlinpoet.FileSpec
import java.io.File
import java.io.InputStream
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

data class ResourceFile(
    val inputStream: InputStream,
    val fileName: String
) : GeneratedFile() {
    override fun writeFileTo(outputDir: File) = inputStream.writeFileTo(Path.of(outputDir.absolutePath, fileName))
}

data class KotlinSourceSet(val files: Collection<FileSpec>, val srcDir: String = MAIN_KT_SOURCE) : GeneratedFile() {
    override fun writeFileTo(outputDir: File) = files.distinct().forEach { it.writeTo(outputDir.resolve(srcDir)) }
}

data class ResourceSourceSet(val files: Collection<ResourceFile>, val srcDir: String = MAIN_RESOURCES) : GeneratedFile() {
    override fun writeFileTo(outputDir: File) {
        with(outputDir.resolve(srcDir)) {
            mkdirs()
            files.forEach { it.writeFileTo(this) }
        }
    }
}
