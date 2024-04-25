package ie.zalando.controllers

import io.ktor.http.Headers
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.Any
import kotlin.ByteArray
import kotlin.String

public interface BinaryDataController {
    /**
     * Route is expected to respond with [kotlin.ByteArray].
     * Use [io.ktor.server.response.respond] to send the response.
     *
     * @param applicationOctetStream
     * @param call The Ktor application call
     */
    public suspend fun postBinaryData(applicationOctetStream: ByteArray, call: ApplicationCall)

    public companion object {
        /**
         * Mounts all routes for the BinaryData resource
         *
         * - POST /binary-data
         */
        public fun Route.binaryDataRoutes(controller: BinaryDataController) {
            post("/binary-data") {
                val applicationOctetStream = call.receive<ByteArray>()
                controller.postBinaryData(applicationOctetStream, call)
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
