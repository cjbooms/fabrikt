package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.InstantDateTimeController
import com.example.controllers.TypedApplicationCall
import com.example.models.QueryResult
import io.mockk.CapturingSlot
import kotlinx.datetime.Instant

class InstantDateTimeControllerImpl(
    private val listCapturingSlot: CapturingSlot<List<Instant>?>,
    private val param2CapturingSlot: CapturingSlot<Instant?>
): InstantDateTimeController {
    override suspend fun get(
        explodeListQueryParam: List<Instant>?,
        queryParam2: Instant?,
        call: TypedApplicationCall<QueryResult>
    ) {
        listCapturingSlot.captured = explodeListQueryParam
        param2CapturingSlot.captured = queryParam2

        call.respondTyped(QueryResult(items = emptyList()))
    }

}