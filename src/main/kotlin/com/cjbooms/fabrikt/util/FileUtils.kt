package com.cjbooms.fabrikt.util

import java.io.InputStream
import java.nio.file.Path

object FileUtils {

    fun InputStream.writeFileTo(path: Path) {
        path.toFile().outputStream().use { this.copyTo(it) }
    }
}
