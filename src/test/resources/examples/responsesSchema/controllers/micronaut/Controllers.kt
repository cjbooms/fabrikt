package examples.responsesSchema.controllers

import examples.responsesSchema.models.SuccessResponseJson
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Produces

@Controller
interface FooController {
    /**
     *
     */
    @Post(uri = "/foo")
    @Produces(value = ["application/json"])
    fun post(): HttpResponse<SuccessResponseJson>
}
