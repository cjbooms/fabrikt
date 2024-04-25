package com.cjbooms.fabrikt.util

import java.io.FileNotFoundException
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readText

object ResourceHelper {
    fun readTextResource(path: String): String =
        (javaClass.getResource(path) ?: throw FileNotFoundException(path)).readText()

    fun readFolder(path: Path): Map<String, String> =
        path.listDirectoryEntries().associate { it.name to it.readText() }
}
