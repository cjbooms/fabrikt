package com.cjbooms.fabrikt.model

import java.nio.file.Path
import java.nio.file.Paths

object Destinations {
    val PATH_ROOT: Path = Paths.get("")

    const val MAIN_KT_SOURCE = "src/main/kotlin/"
    val MAIN_KT_SRC: Path = PATH_ROOT.resolve(MAIN_KT_SOURCE)
    private const val MAIN_RESOURCES_STRING = "src/main/resources"
    val MAIN_RESOURCES: Path = PATH_ROOT.resolve(MAIN_RESOURCES_STRING)

    fun entitiesPackage(basePackage: String): String = "$basePackage.entities"
    fun repositoriesPackage(basePackage: String): String = "$basePackage.repositories"
    fun servicesPackage(basePackage: String): String = "$basePackage.service"
    fun servicesImplPackage(basePackage: String): String = "$basePackage.service.impl"
    fun modelsPackage(basePackage: String): String = "$basePackage.models"
    fun controllersPackage(basePackage: String): String = "$basePackage.controllers"
    fun convertersPackage(basePackage: String): String = "$basePackage.converters"
    fun utilsPackage(basePackage: String) = "$basePackage.utils"
    fun extensionsPackage(basePackage: String) = "$basePackage.extensions"
    fun clientPackage(basePackage: String) = "$basePackage.client"
    fun configurationPackage(basePackage: String) = "$basePackage.configuration"
}
