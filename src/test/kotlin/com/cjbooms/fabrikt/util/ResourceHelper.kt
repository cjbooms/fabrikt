package com.cjbooms.fabrikt.util

import java.io.FileNotFoundException

object ResourceHelper {
    fun readTextResource(path: String): String =
        (javaClass.getResource(path) ?: throw FileNotFoundException(path)).readText()
}
