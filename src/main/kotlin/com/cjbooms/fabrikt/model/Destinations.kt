package com.cjbooms.fabrikt.model

import java.nio.file.Path
import java.nio.file.Paths

object Destinations {
    val PATH_ROOT: Path = Paths.get("")

    const val MAIN_KT_SOURCE = "src/main/kotlin/"
    const val MAIN_RESOURCES = "src/main/resources"
    val MAIN_KT_SRC: Path = PATH_ROOT.resolve(MAIN_KT_SOURCE)

    fun modelsPackage(basePackage: String): String = "$basePackage.models"
    fun controllersPackage(basePackage: String): String = "$basePackage.controllers"
    fun clientPackage(basePackage: String) = "$basePackage.client"
}
