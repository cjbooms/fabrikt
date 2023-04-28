package examples.requestsSchema.controllers

import examples.requestsSchema.models.FooPostApplicationJsonDummyRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import javax.validation.Valid
import kotlin.Unit

@Controller
interface FooController {
    /**
     *
     *
     * @param dummyRequest Request body
     */
    @Post(uri = "/foo")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        dummyRequest: FooPostApplicationJsonDummyRequest
    ): HttpResponse<Unit>
}
