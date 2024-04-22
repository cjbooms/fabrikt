package examples.pathLevelParameters.controllers

import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.`get`
import io.ktor.server.util.getOrFail
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.Any
import kotlin.String
import kotlin.Unit

public interface ExampleController {
    /**
     *
     *
     * @param a
     * @param b
     */
    public suspend fun `get`(
        call: ApplicationCall,
        a: String,
        b: String,
    ): ControllerResult<Unit>

    public companion object {
        public fun Route.exampleRoutes(controller: ExampleController) {
            get("/example") {
                val a = call.request.queryParameters.getOrFail<kotlin.String>("a")
                val b = call.request.queryParameters.getOrFail<kotlin.String>("b")
                val result = controller.get(call, a, b)
                call.respond(result.status)
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

public data class ControllerResult<T>(
    public val status: HttpStatusCode,
    public val message: T,
)
