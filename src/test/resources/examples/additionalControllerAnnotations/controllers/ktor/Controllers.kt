package ie.zalando.controllers

import example.Annotation1
import example.Annotation2
import ie.zalando.models.Content
import ie.zalando.models.FirstModel
import ie.zalando.models.QueryResult
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.`get`
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.util.converters.ConversionService
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.Any
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List

@Annotation1
@Annotation2
public interface ExamplePath1Controller {
    /**
     * GET example path 1
     *
     * Route is expected to respond with [ie.zalando.models.QueryResult].
     * Use [ie.zalando.controllers.TypedApplicationCall.respondTyped] to send the response.
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param intListQueryParam
     * @param call Decorated ApplicationCall with additional typed respond methods
     */
    public suspend fun `get`(
        explodeListQueryParam: List<String>?,
        queryParam2: Int?,
        intListQueryParam: List<Int>?,
        call: TypedApplicationCall<QueryResult>,
    )

    /**
     * POST example path 1
     *
     * Route is expected to respond with status 201.
     * Use [respond] to send the response.
     *
     * @param content
     * @param explodeListQueryParam
     * @param call The Ktor application call
     */
    public suspend fun post(
        explodeListQueryParam: List<String>?,
        content: Content,
        call: ApplicationCall,
    )

    public companion object {
        /**
         * Mounts all routes for the ExamplePath1 resource
         *
         * - GET /example-path-1 GET example path 1
         * - POST /example-path-1 POST example path 1
         */
        public fun Route.examplePath1Routes(controller: ExamplePath1Controller) {
            `get`("/example-path-1") {
                val explodeListQueryParam =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.String>>("explode_list_query_param")
                val queryParam2 = call.request.queryParameters.getTyped<kotlin.Int>("query_param2")
                val intListQueryParam =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.Int>>("int_list_query_param")
                controller.get(
                    explodeListQueryParam,
                    queryParam2,
                    intListQueryParam,
                    TypedApplicationCall(call),
                )
            }
            post("/example-path-1") {
                val explodeListQueryParam =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.String>>("explode_list_query_param")
                val content = call.receive<Content>()
                controller.post(explodeListQueryParam, content, call)
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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
         * Gets parameter value associated with this name or throws if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   MissingRequestParameterException - when parameter is missing
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTypedOrFail(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R {
            val values = getAll(name) ?: throw MissingRequestParameterException(name)
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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

@Annotation1
@Annotation2
public interface ExamplePath2Controller {
    /**
     * GET example path 2
     *
     * Route is expected to respond with [ie.zalando.models.Content].
     * Use [ie.zalando.controllers.TypedApplicationCall.respondTyped] to send the response.
     *
     * @param pathParam The resource id
     * @param limit
     * @param queryParam2
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     * @param call Decorated ApplicationCall with additional typed respond methods
     */
    public suspend fun getById(
        ifNoneMatch: String?,
        pathParam: String,
        limit: Int?,
        queryParam2: Int?,
        call: TypedApplicationCall<Content>,
    )

    /**
     * PUT example path 2
     *
     * Route is expected to respond with status 204.
     * Use [respond] to send the response.
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     * @param call The Ktor application call
     */
    public suspend fun putById(
        ifMatch: String,
        pathParam: String,
        firstModel: FirstModel,
        call: ApplicationCall,
    )

    public companion object {
        /**
         * Mounts all routes for the ExamplePath2 resource
         *
         * - GET /example-path-2/{path_param} GET example path 2
         * - PUT /example-path-2/{path_param} PUT example path 2
         */
        public fun Route.examplePath2Routes(controller: ExamplePath2Controller) {
            `get`("/example-path-2/{path_param}") {
                val pathParam = call.parameters.getTypedOrFail<kotlin.String>("path_param")
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val queryParam2 = call.request.queryParameters.getTyped<kotlin.Int>("query_param2")
                controller.getById(ifNoneMatch, pathParam, limit, queryParam2, TypedApplicationCall(call))
            }
            put("/example-path-2/{path_param}") {
                val pathParam = call.parameters.getTypedOrFail<kotlin.String>("path_param")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val firstModel = call.receive<FirstModel>()
                controller.putById(ifMatch, pathParam, firstModel, call)
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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
         * Gets parameter value associated with this name or throws if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   MissingRequestParameterException - when parameter is missing
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTypedOrFail(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R {
            val values = getAll(name) ?: throw MissingRequestParameterException(name)
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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

@Annotation1
@Annotation2
public interface ExamplePath3SubresourceController {
    /**
     * PUT example path 3
     *
     * Route is expected to respond with status 204.
     * Use [respond] to send the response.
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     * @param csvListQueryParam
     * @param call The Ktor application call
     */
    public suspend fun put(
        ifMatch: String,
        pathParam: String,
        csvListQueryParam: List<String>?,
        firstModel: FirstModel,
        call: ApplicationCall,
    )

    public companion object {
        /**
         * Mounts all routes for the ExamplePath3Subresource resource
         *
         * - PUT /example-path-3/{path_param}/subresource PUT example path 3
         */
        public fun Route.examplePath3SubresourceRoutes(controller: ExamplePath3SubresourceController) {
            put("/example-path-3/{path_param}/subresource") {
                val pathParam = call.parameters.getTypedOrFail<kotlin.String>("path_param")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val csvListQueryParam =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.String>>("csv_list_query_param")
                val firstModel = call.receive<FirstModel>()
                controller.put(ifMatch, pathParam, csvListQueryParam, firstModel, call)
            }
        }

        /**
         * Gets parameter value associated with this name or null if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTyped(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R? {
            val values = getAll(name) ?: return null
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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
         * Gets parameter value associated with this name or throws if the name is not present.
         * Converting to type R using ConversionService.
         *
         * Throws:
         *   MissingRequestParameterException - when parameter is missing
         *   ParameterConversionException - when conversion from String to R fails
         */
        private inline fun <reified R : Any> Parameters.getTypedOrFail(
            name: String,
            conversionService: ConversionService = DefaultConversionService,
        ): R {
            val values = getAll(name) ?: throw MissingRequestParameterException(name)
            val typeInfo = typeInfo<R>()
            return try {
                @Suppress("UNCHECKED_CAST")
                conversionService.fromValues(values, typeInfo) as R
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

/**
 * Decorator for Ktor's ApplicationCall that provides type safe variants of the [respond] functions.
 *
 * It can be used as a drop-in replacement for [io.ktor.server.application.ApplicationCall].
 *
 * @param R The type of the response body
 */
public class TypedApplicationCall<R : Any>(
    private val applicationCall: ApplicationCall,
) : ApplicationCall by applicationCall {
    @Suppress("unused")
    public suspend inline fun <reified T : R> respondTyped(message: T) {
        respond(message)
    }

    @Suppress("unused")
    public suspend inline fun <reified T : R> respondTyped(status: HttpStatusCode, message: T) {
        respond(status, message)
    }
}
