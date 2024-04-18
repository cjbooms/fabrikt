package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.ContributorsController
import com.example.controllers.TypedApplicationCall
import com.example.models.Contributor
import com.example.models.ContributorQueryResult
import com.example.models.StatusQueryParam
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.mockk.CapturingSlot

/**
 * This is a test implementation of the ContributorsController interface that captures the values of the parameters
 * in CapturingSlots so that they can be asserted against in tests.
 */
class ContributorsControllerImpl(
    private val xFlowIdCapturingSlot: CapturingSlot<String?>,
    private val limitCapturingSlot: CapturingSlot<Int?>,
) : ContributorsController {
    override suspend fun searchContributors(
        xFlowId: String?,
        limit: Int?,
        includeInactive: Boolean?,
        cursor: String?,
        call: TypedApplicationCall<ContributorQueryResult>
    ) {
        xFlowIdCapturingSlot.captured = xFlowId
        limitCapturingSlot.captured = limit

        call.respondTyped(ContributorQueryResult(null, null, emptyList()))
    }

    override suspend fun createContributor(
        xFlowId: String?,
        idempotencyKey: String?,
        contributor: Contributor,
        call: ApplicationCall
    ) {
        call.respond(HttpStatusCode.Created)
    }

    override suspend fun getContributor(
        xFlowId: String?,
        ifNoneMatch: String?,
        id: String,
        status: StatusQueryParam?,
        call: TypedApplicationCall<Contributor>
    ) {
        // does not respond, should result in 404 Not Found
    }

    override suspend fun putById(
        ifMatch: String,
        xFlowId: String?,
        idempotencyKey: String?,
        id: String,
        contributor: Contributor,
        call: ApplicationCall
    ) {
        call.respond(HttpStatusCode.NoContent)
    }
}
