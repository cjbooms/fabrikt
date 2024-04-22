package examples.githubApi.controllers

import examples.githubApi.models.BulkEntityDetails
import examples.githubApi.models.Contributor
import examples.githubApi.models.ContributorQueryResult
import examples.githubApi.models.EventResults
import examples.githubApi.models.Organisation
import examples.githubApi.models.OrganisationQueryResult
import examples.githubApi.models.PullRequest
import examples.githubApi.models.PullRequestQueryResult
import examples.githubApi.models.Repository
import examples.githubApi.models.RepositoryQueryResult
import examples.githubApi.models.StatusQueryParam
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.ParameterConversionException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.`get`
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.util.getOrFail
import io.ktor.util.converters.DefaultConversionService
import io.ktor.util.reflect.typeInfo
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List

public interface InternalEventsController {
    /**
     * Generate change events for a list of entities
     *
     * @param bulkEntityDetails
     */
    public suspend fun post(call: ApplicationCall, bulkEntityDetails: BulkEntityDetails): ControllerResult<EventResults>

    public companion object {
        public fun Route.internalEventsRoutes(controller: InternalEventsController) {
            post("/internal/events") {
                val bulkEntityDetails = call.receive<BulkEntityDetails>()
                val result = controller.post(call, bulkEntityDetails)
                call.respond(result.status, result.message)
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

public interface ContributorsController {
    /**
     * Page through all the Contributor resources matching the query filters
     *
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param includeInactive A query parameter to request both active and inactive entities
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    public suspend fun searchContributors(
        call: ApplicationCall,
        xFlowId: String?,
        limit: Int?,
        includeInactive: Boolean?,
        cursor: String?,
    ): ControllerResult<ContributorQueryResult>

    /**
     * Create a new Contributor
     *
     * @param contributor
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun createContributor(
        call: ApplicationCall,
        xFlowId: String?,
        idempotencyKey: String?,
        contributor: Contributor,
    )

    /**
     * Get a Contributor by ID
     *
     * @param id The unique id for the resource
     * @param status Changes the behavior of GET | HEAD based on the value of the status property.
     * Will return a 404 if the status property does not match the query parameter."
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param ifNoneMatch The RFC7232 If-None-Match header field in a request requires the server to
     * only operate on the resource if it does not match any of the provided entity-tags. If the provided
     * entity-tag is `*`, it is required that the resource does not exist at all.
     *
     */
    public suspend fun getContributor(
        call: ApplicationCall,
        xFlowId: String?,
        ifNoneMatch: String?,
        id: String,
        status: StatusQueryParam?,
    ): ControllerResult<Contributor>

    /**
     * Update an existing Contributor
     *
     * @param contributor
     * @param id The unique id for the resource
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun putById(
        call: ApplicationCall,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        id: String,
        contributor: Contributor,
    )

    public companion object {
        public fun Route.contributorsRoutes(controller: ContributorsController) {
            `get`("/contributors") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val includeInactive =
                    call.request.queryParameters.getTyped<kotlin.Boolean>("include_inactive")
                val cursor = call.request.queryParameters.getTyped<kotlin.String>("cursor")
                val result = controller.searchContributors(call, xFlowId, limit, includeInactive, cursor)
                call.respond(result.status, result.message)
            }
            post("/contributors") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val contributor = call.receive<Contributor>()
                controller.createContributor(call, xFlowId, idempotencyKey, contributor)
            }
            `get`("/contributors/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val status =
                    call.request.queryParameters.getTyped<examples.githubApi.models.StatusQueryParam>("status")
                val result = controller.getContributor(call, xFlowId, ifNoneMatch, id, status)
                call.respond(result.status, result.message)
            }
            put("/contributors/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val contributor = call.receive<Contributor>()
                controller.putById(call, ifMatch, xFlowId, idempotencyKey, id, contributor)
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

public interface OrganisationsController {
    /**
     * Page through all the Organisation resources matching the query filters
     *
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param includeInactive A query parameter to request both active and inactive entities
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    public suspend fun `get`(
        call: ApplicationCall,
        xFlowId: String?,
        limit: Int?,
        includeInactive: Boolean?,
        cursor: String?,
    ): ControllerResult<OrganisationQueryResult>

    /**
     * Create a new Organisation
     *
     * @param organisation
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun post(
        call: ApplicationCall,
        xFlowId: String?,
        idempotencyKey: String?,
        organisation: Organisation,
    )

    /**
     * Get a Organisation by ID
     *
     * @param id The unique id for the resource
     * @param status Changes the behavior of GET | HEAD based on the value of the status property.
     * Will return a 404 if the status property does not match the query parameter."
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param ifNoneMatch The RFC7232 If-None-Match header field in a request requires the server to
     * only operate on the resource if it does not match any of the provided entity-tags. If the provided
     * entity-tag is `*`, it is required that the resource does not exist at all.
     *
     */
    public suspend fun getById(
        call: ApplicationCall,
        xFlowId: String?,
        ifNoneMatch: String?,
        id: String,
        status: StatusQueryParam?,
    ): ControllerResult<Organisation>

    /**
     * Update an existing Organisation
     *
     * @param organisation
     * @param id The unique id for the resource
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun putById(
        call: ApplicationCall,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        id: String,
        organisation: Organisation,
    )

    public companion object {
        public fun Route.organisationsRoutes(controller: OrganisationsController) {
            `get`("/organisations") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val includeInactive =
                    call.request.queryParameters.getTyped<kotlin.Boolean>("include_inactive")
                val cursor = call.request.queryParameters.getTyped<kotlin.String>("cursor")
                val result = controller.get(call, xFlowId, limit, includeInactive, cursor)
                call.respond(result.status, result.message)
            }
            post("/organisations") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val organisation = call.receive<Organisation>()
                controller.post(call, xFlowId, idempotencyKey, organisation)
            }
            `get`("/organisations/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val status =
                    call.request.queryParameters.getTyped<examples.githubApi.models.StatusQueryParam>("status")
                val result = controller.getById(call, xFlowId, ifNoneMatch, id, status)
                call.respond(result.status, result.message)
            }
            put("/organisations/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val organisation = call.receive<Organisation>()
                controller.putById(call, ifMatch, xFlowId, idempotencyKey, id, organisation)
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

public interface OrganisationsContributorsController {
    /**
     * Page through all the Contributor resources for this parent Organisation matching the query
     * filters
     *
     * @param parentId The unique id for the parent resource
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param includeInactive A query parameter to request both active and inactive entities
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    public suspend fun `get`(
        call: ApplicationCall,
        xFlowId: String?,
        parentId: String,
        limit: Int?,
        includeInactive: Boolean?,
        cursor: String?,
    ): ControllerResult<ContributorQueryResult>

    /**
     * Get a Contributor for this Organisation by ID
     *
     * @param parentId The unique id for the parent resource
     * @param id The unique id for the resource
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param ifNoneMatch The RFC7232 If-None-Match header field in a request requires the server to
     * only operate on the resource if it does not match any of the provided entity-tags. If the provided
     * entity-tag is `*`, it is required that the resource does not exist at all.
     *
     */
    public suspend fun getById(
        call: ApplicationCall,
        xFlowId: String?,
        ifNoneMatch: String?,
        parentId: String,
        id: String,
    ): ControllerResult<Contributor>

    /**
     * Add an existing Contributor to this Organisation
     *
     * @param parentId The unique id for the parent resource
     * @param id The unique id for the resource
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun putById(
        call: ApplicationCall,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        parentId: String,
        id: String,
    )

    /**
     * Remove Contributor from this Organisation. Does not delete the underlying Contributor.
     *
     * @param parentId The unique id for the parent resource
     * @param id The unique id for the resource
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     */
    public suspend fun deleteById(
        call: ApplicationCall,
        xFlowId: String?,
        parentId: String,
        id: String,
    )

    public companion object {
        public fun Route.organisationsContributorsRoutes(controller: OrganisationsContributorsController) {
            `get`("/organisations/{parent-id}/contributors") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val includeInactive =
                    call.request.queryParameters.getTyped<kotlin.Boolean>("include_inactive")
                val cursor = call.request.queryParameters.getTyped<kotlin.String>("cursor")
                val result = controller.get(call, xFlowId, parentId, limit, includeInactive, cursor)
                call.respond(result.status, result.message)
            }
            `get`("/organisations/{parent-id}/contributors/{id}") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val result = controller.getById(call, xFlowId, ifNoneMatch, parentId, id)
                call.respond(result.status, result.message)
            }
            put("/organisations/{parent-id}/contributors/{id}") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                controller.putById(call, ifMatch, xFlowId, idempotencyKey, parentId, id)
            }
            delete("/organisations/{parent-id}/contributors/{id}") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                controller.deleteById(call, xFlowId, parentId, id)
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

public interface RepositoriesController {
    /**
     * Page through all the Repository resources matching the query filters
     *
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param slug Filters resources by the [slug] property. Accepts comma-delimited values
     *
     * @param name Filters resources by the [name] property. Accepts comma-delimited values
     *
     * @param includeInactive A query parameter to request both active and inactive entities
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    public suspend fun `get`(
        call: ApplicationCall,
        xFlowId: String?,
        limit: Int?,
        slug: List<String>?,
        name: List<String>?,
        includeInactive: Boolean?,
        cursor: String?,
    ): ControllerResult<RepositoryQueryResult>

    /**
     * Create a new Repository
     *
     * @param repository
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun post(
        call: ApplicationCall,
        xFlowId: String?,
        idempotencyKey: String?,
        repository: Repository,
    )

    /**
     * Get a Repository by ID
     *
     * @param id The unique id for the resource
     * @param status Changes the behavior of GET | HEAD based on the value of the status property.
     * Will return a 404 if the status property does not match the query parameter."
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param ifNoneMatch The RFC7232 If-None-Match header field in a request requires the server to
     * only operate on the resource if it does not match any of the provided entity-tags. If the provided
     * entity-tag is `*`, it is required that the resource does not exist at all.
     *
     */
    public suspend fun getById(
        call: ApplicationCall,
        xFlowId: String?,
        ifNoneMatch: String?,
        id: String,
        status: StatusQueryParam?,
    ): ControllerResult<Repository>

    /**
     * Update an existing Repository
     *
     * @param repository
     * @param id The unique id for the resource
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun putById(
        call: ApplicationCall,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        id: String,
        repository: Repository,
    )

    public companion object {
        public fun Route.repositoriesRoutes(controller: RepositoriesController) {
            `get`("/repositories") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val slug =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.String>>("slug")
                val name =
                    call.request.queryParameters.getTyped<kotlin.collections.List<kotlin.String>>("name")
                val includeInactive =
                    call.request.queryParameters.getTyped<kotlin.Boolean>("include_inactive")
                val cursor = call.request.queryParameters.getTyped<kotlin.String>("cursor")
                val result = controller.get(call, xFlowId, limit, slug, name, includeInactive, cursor)
                call.respond(result.status, result.message)
            }
            post("/repositories") {
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val repository = call.receive<Repository>()
                controller.post(call, xFlowId, idempotencyKey, repository)
            }
            `get`("/repositories/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val status =
                    call.request.queryParameters.getTyped<examples.githubApi.models.StatusQueryParam>("status")
                val result = controller.getById(call, xFlowId, ifNoneMatch, id, status)
                call.respond(result.status, result.message)
            }
            put("/repositories/{id}") {
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val repository = call.receive<Repository>()
                controller.putById(call, ifMatch, xFlowId, idempotencyKey, id, repository)
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

public interface RepositoriesPullRequestsController {
    /**
     * Page through all the PullRequest resources for this parent Repository matching the query
     * filters
     *
     * @param parentId The unique id for the parent resource
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param includeInactive A query parameter to request both active and inactive entities
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    public suspend fun `get`(
        call: ApplicationCall,
        xFlowId: String?,
        parentId: String,
        limit: Int?,
        includeInactive: Boolean?,
        cursor: String?,
    ): ControllerResult<PullRequestQueryResult>

    /**
     * Create a new PullRequest for this parent Repository
     *
     * @param pullRequest
     * @param parentId The unique id for the parent resource
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun post(
        call: ApplicationCall,
        xFlowId: String?,
        idempotencyKey: String?,
        parentId: String,
        pullRequest: PullRequest,
    )

    /**
     * Get a PullRequest for this Repository by ID
     *
     * @param parentId The unique id for the parent resource
     * @param id The unique id for the resource
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param ifNoneMatch The RFC7232 If-None-Match header field in a request requires the server to
     * only operate on the resource if it does not match any of the provided entity-tags. If the provided
     * entity-tag is `*`, it is required that the resource does not exist at all.
     *
     */
    public suspend fun getById(
        call: ApplicationCall,
        xFlowId: String?,
        ifNoneMatch: String?,
        parentId: String,
        id: String,
    ): ControllerResult<PullRequest>

    /**
     * Update the PullRequest owned by this Repository
     *
     * @param pullRequest
     * @param parentId The unique id for the parent resource
     * @param id The unique id for the resource
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param idempotencyKey This unique identifier can be used to allow for clients to safely retry
     * requests without accidentally performing the same operation twice. This is useful in cases such as
     * network failures. This Id should remain the same for a given operation, so that the server can use
     * it to recognise subsequent retries of the same request. Clients should be careful in using this
     * key as subsequent requests with the same key return the same result. How the key is generated is
     * up to the client, but it is suggested to use UUID (v4), or any other random string with enough
     * entropy to avoid collisions.
     *
     */
    public suspend fun putById(
        call: ApplicationCall,
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        parentId: String,
        id: String,
        pullRequest: PullRequest,
    )

    public companion object {
        public fun Route.repositoriesPullRequestsRoutes(controller: RepositoriesPullRequestsController) {
            `get`("/repositories/{parent-id}/pull-requests") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val limit = call.request.queryParameters.getTyped<kotlin.Int>("limit")
                val includeInactive =
                    call.request.queryParameters.getTyped<kotlin.Boolean>("include_inactive")
                val cursor = call.request.queryParameters.getTyped<kotlin.String>("cursor")
                val result = controller.get(call, xFlowId, parentId, limit, includeInactive, cursor)
                call.respond(result.status, result.message)
            }
            post("/repositories/{parent-id}/pull-requests") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val pullRequest = call.receive<PullRequest>()
                controller.post(call, xFlowId, idempotencyKey, parentId, pullRequest)
            }
            `get`("/repositories/{parent-id}/pull-requests/{id}") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val ifNoneMatch = call.request.headers["If-None-Match"]
                val result = controller.getById(call, xFlowId, ifNoneMatch, parentId, id)
                call.respond(result.status, result.message)
            }
            put("/repositories/{parent-id}/pull-requests/{id}") {
                val parentId = call.parameters.getOrFail<kotlin.String>("parent-id")
                val id = call.parameters.getOrFail<kotlin.String>("id")
                val ifMatch = call.request.headers.getOrFail("If-Match")
                val xFlowId = call.request.headers["X-Flow-Id"]
                val idempotencyKey = call.request.headers["Idempotency-Key"]
                val pullRequest = call.receive<PullRequest>()
                controller.putById(call, ifMatch, xFlowId, idempotencyKey, parentId, id, pullRequest)
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
