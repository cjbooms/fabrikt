package com.cjbooms.fabrikt.configurations

import com.cjbooms.fabrikt.model.Destinations.clientPackage
import com.cjbooms.fabrikt.model.Destinations.controllersPackage
import com.cjbooms.fabrikt.model.Destinations.modelsPackage
import com.cjbooms.fabrikt.model.Destinations.servicesPackage

class Packages(val base: String) {
    val models: String = modelsPackage(base)
    val client: String = clientPackage(base)
    val services: String = servicesPackage(base)
    val controllers: String = controllersPackage(base)
}
