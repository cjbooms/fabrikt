package examples.authentication.controllers

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.auth.Principal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.routing.Route
import io.ktor.server.routing.`get`
import io.ktor.server.util.getOrFail
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.Any
import kotlin.IllegalStateException
import kotlin.String

public interface RequiredController {
    /**
     * Route is expected to respond with status 200.
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param testString
     * @param call The Ktor application call
     */
    public suspend fun testPath(
        testString: String,
        principal: Principal,
        call: ApplicationCall,
    )

    public companion object {
        public fun Route.requiredRoutes(controller: RequiredController) {
            authenticate("BasicAuth", optional = false) {
                `get`("/required") {
                    val principal = call.principal<Principal>() ?: throw
                        IllegalStateException("Principal not found")
                    val testString = call.request.queryParameters.getOrFail<kotlin.String>("testString")
                    controller.testPath(testString, principal, call)
                }
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using DefaultConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                DefaultConversionService.fromValues(values, typeInfo) as R
            } catch (cause: Exception) {
                throw ParameterConversionException(
                    name,
                    typeInfo.type.simpleName
                        ?: typeInfo.type.toString(),
                    cause,
                )
            }
        }

        /**
         * Gets first value from the list of values associated with a name.
         *
         * Throws:
         *   BadRequestException - when the name is not present
         */
        private fun Headers.getOrFail(name: String): String = this[name] ?: throw
            BadRequestException("Header " + name + " is required")
    }
}

public interface ProhibitedController {
    /**
     * Route is expected to respond with status 200.
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param testString
     * @param call The Ktor application call
     */
    public suspend fun testPath(testString: String, call: ApplicationCall)

    public companion object {
        public fun Route.prohibitedRoutes(controller: ProhibitedController) {
            `get`("/prohibited") {
                val testString = call.request.queryParameters.getOrFail<kotlin.String>("testString")
                controller.testPath(testString, call)
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using DefaultConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                DefaultConversionService.fromValues(values, typeInfo) as R
            } catch (cause: Exception) {
                throw ParameterConversionException(
                    name,
                    typeInfo.type.simpleName
                        ?: typeInfo.type.toString(),
                    cause,
                )
            }
        }

        /**
         * Gets first value from the list of values associated with a name.
         *
         * Throws:
         *   BadRequestException - when the name is not present
         */
        private fun Headers.getOrFail(name: String): String = this[name] ?: throw
            BadRequestException("Header " + name + " is required")
    }
}

public interface OptionalController {
    /**
     * Route is expected to respond with status 200.
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param testString
     * @param call The Ktor application call
     */
    public suspend fun testPath(
        testString: String,
        principal: Principal?,
        call: ApplicationCall,
    )

    public companion object {
        public fun Route.optionalRoutes(controller: OptionalController) {
            authenticate("BasicAuth", optional = true) {
                `get`("/optional") {
                    val principal = call.principal<Principal>()
                    val testString = call.request.queryParameters.getOrFail<kotlin.String>("testString")
                    controller.testPath(testString, principal, call)
                }
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using DefaultConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                DefaultConversionService.fromValues(values, typeInfo) as R
            } catch (cause: Exception) {
                throw ParameterConversionException(
                    name,
                    typeInfo.type.simpleName
                        ?: typeInfo.type.toString(),
                    cause,
                )
            }
        }

        /**
         * Gets first value from the list of values associated with a name.
         *
         * Throws:
         *   BadRequestException - when the name is not present
         */
        private fun Headers.getOrFail(name: String): String = this[name] ?: throw
            BadRequestException("Header " + name + " is required")
    }
}

public interface NoneController {
    /**
     * Route is expected to respond with status 200.
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param testString
     * @param call The Ktor application call
     */
    public suspend fun testPath(testString: String, call: ApplicationCall)

    public companion object {
        public fun Route.noneRoutes(controller: NoneController) {
            `get`("/none") {
                val testString = call.request.queryParameters.getOrFail<kotlin.String>("testString")
                controller.testPath(testString, call)
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using DefaultConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                DefaultConversionService.fromValues(values, typeInfo) as R
            } catch (cause: Exception) {
                throw ParameterConversionException(
                    name,
                    typeInfo.type.simpleName
                        ?: typeInfo.type.toString(),
                    cause,
                )
            }
        }

        /**
         * Gets first value from the list of values associated with a name.
         *
         * Throws:
         *   BadRequestException - when the name is not present
         */
        private fun Headers.getOrFail(name: String): String = this[name] ?: throw
            BadRequestException("Header " + name + " is required")
    }
}

public interface DefaultController {
    /**
     * Route is expected to respond with status 200.
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param testString
     * @param call The Ktor application call
     */
    public suspend fun testPath(
        testString: String,
        principal: Principal,
        call: ApplicationCall,
    )

    public companion object {
        public fun Route.defaultRoutes(controller: DefaultController) {
            authenticate("basicAuth", optional = false) {
                `get`("/default") {
                    val principal = call.principal<Principal>() ?: throw
                        IllegalStateException("Principal not found")
                    val testString = call.request.queryParameters.getOrFail<kotlin.String>("testString")
                    controller.testPath(testString, principal, call)
                }
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using DefaultConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(name: String): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                DefaultConversionService.fromValues(values, typeInfo) as R
            } catch (cause: Exception) {
                throw ParameterConversionException(
                    name,
                    typeInfo.type.simpleName
                        ?: typeInfo.type.toString(),
                    cause,
                )
            }
        }

        /**
         * Gets first value from the list of values associated with a name.
         *
         * Throws:
         *   BadRequestException - when the name is not present
         */
        private fun Headers.getOrFail(name: String): String = this[name] ?: throw
            BadRequestException("Header " + name + " is required")
    }
}
