package com.cjbooms.fabrikt.model

import java.nio.file.Path

object Destinations {

    val MAIN_KT_SOURCE: Path = Path.of("/src/main/kotlin/")
    val MAIN_RESOURCES: Path = Path.of("/src/main/resources")

    fun modelsPackage(basePackage: String): String = "$basePackage.models"
    fun controllersPackage(basePackage: String): String = "$basePackage.controllers"
    fun clientPackage(basePackage: String) = "$basePackage.client"
}
