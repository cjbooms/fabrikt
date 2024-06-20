package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.InternalEventsController
import com.example.controllers.TypedApplicationCall
import com.example.models.BulkEntityDetails
import com.example.models.Event
import com.example.models.EventResults
import io.ktor.http.HttpStatusCode
import io.mockk.CapturingSlot

/**
 * This is a test implementation of the [InternalEventsController] interface that captures the request body
 * and returns a fixed response.
 */
class InternalEventsControllerImpl(
    private val bodyCapturingSlot: CapturingSlot<BulkEntityDetails>,
) : InternalEventsController {
    override suspend fun post(bulkEntityDetails: BulkEntityDetails, call: TypedApplicationCall<EventResults>) {
        bodyCapturingSlot.captured = bulkEntityDetails

        call.respondTyped(
            HttpStatusCode.OK, EventResults(
                listOf(
                    Event(
                        entityId = "entityId",
                        properties = mutableMapOf(),
                        data = mapOf(
                            "dataKey" to 1,
                            "otherDataKey" to "value",
                        )
                    )
                )
            )
        )
    }
}
