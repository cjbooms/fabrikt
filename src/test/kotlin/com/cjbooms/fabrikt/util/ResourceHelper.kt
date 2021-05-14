package com.cjbooms.fabrikt.util

object ResourceHelper {
    fun readTextResource(path: String): String = javaClass.getResource(path)!!.readText()
}
