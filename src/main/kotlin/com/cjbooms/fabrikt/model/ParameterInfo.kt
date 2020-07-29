package com.cjbooms.fabrikt.model

sealed class ParameterLocation {
    companion object {
        fun getLocation(location: String): ParameterLocation {
            return when (location) {
                "body" -> BodyParam
                "query" -> QueryParam
                "header" -> HeaderParam
                "path" -> PathParam
                else -> throw IllegalStateException("Unknown parameter location: $location")
            }
        }
    }
}

object BodyParam : ParameterLocation()
object QueryParam : ParameterLocation()
object HeaderParam : ParameterLocation()
object PathParam : ParameterLocation()
