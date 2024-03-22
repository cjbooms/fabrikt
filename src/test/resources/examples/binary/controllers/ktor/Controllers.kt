package ie.zalando.controllers

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlin.ByteArray

public interface BinaryDataController {
    /**
     *
     *
     * @param applicationOctetStream
     */
    public suspend fun postBinaryData(call: ApplicationCall, applicationOctetStream: ByteArray): ControllerResult<ByteArray>

    public companion object {
        public fun Route.binaryDataRoutes(controller: BinaryDataController) {
            post("/binary-data") {
                val applicationOctetStream = call.receive<ByteArray>()
                val result = controller.postBinaryData(call, applicationOctetStream)
                call.respond(result.status, result.message)
            }
        }
    }
}
