package examples.singleAllOf.controllers

import examples.singleAllOf.models.Result
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces

@Controller
interface TestController {
    /**
     *
     */
    @Get(uri = "/test")
    @Produces(value = ["application/json"])
    fun test(): HttpResponse<Result>
}
