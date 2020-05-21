package com.cjbooms.fabrikt.configurations

import com.cjbooms.fabrikt.model.Destinations.clientPackage
import com.cjbooms.fabrikt.model.Destinations.modelsPackage

data class PackagesConfig(val packages: Packages) {
    companion object {
        fun build(basePackage: String) =
            PackagesConfig(
                Packages(
                    base = basePackage,
                    models = modelsPackage(basePackage),
                    client = clientPackage(basePackage)
                )
            )
    }

    data class Packages(
        val base: String,
        val models: String,
        val client: String
    )
}
