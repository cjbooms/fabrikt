package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.PathParamsController
import com.example.models.PathParamWithEnum
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond
import io.mockk.CapturingSlot
import java.util.UUID

class PathParamsControllerImpl(
    private val primitiveParamCapturingSlot: CapturingSlot<String?>,
    private val formatParamCapturingSlot: CapturingSlot<UUID?>,
    private val enumParamCapturingSlot: CapturingSlot<PathParamWithEnum?>,
) : PathParamsController {
    override suspend fun getById(
        primitiveParam: String,
        formatParam: UUID,
        enumParam: PathParamWithEnum,
        call: ApplicationCall
    ) {
        primitiveParamCapturingSlot.captured = primitiveParam
        formatParamCapturingSlot.captured = formatParam
        enumParamCapturingSlot.captured = enumParam

        call.respond(HttpStatusCode.NoContent)
    }
}