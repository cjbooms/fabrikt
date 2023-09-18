package ie.zalando.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Body
import io.micronaut.http.`annotation`.Consumes
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Post
import io.micronaut.http.`annotation`.Produces
import javax.validation.Valid
import kotlin.ByteArray

@Controller
public interface BinaryDataController {
    /**
     *
     *
     * @param applicationOctetStream
     */
    @Post(uri = "/binary-data")
    @Consumes(value = ["application/octet-stream"])
    @Produces(value = ["application/octet-stream"])
    public fun postBinaryData(
        @Body @Valid
        applicationOctetStream: ByteArray,
    ): HttpResponse<ByteArray>
}
