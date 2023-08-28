package examples.pathLevelParameters.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import kotlin.String
import kotlin.Unit

@Controller
interface ExampleController {
    /**
     *
     *
     * @param a
     * @param b
     */
    @Get(uri = "/example")
    fun get(@QueryValue(value = "a") a: String, @QueryValue(value = "b") b: String):
        HttpResponse<Unit>
}
