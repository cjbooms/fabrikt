package examples.requestsSchema.controllers

import examples.requestsSchema.models.DummyRequestJson
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
     * @param dummyRequestJson Request body
     */
    @Post(uri = "/foo")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        dummyRequestJson: DummyRequestJson
    ): HttpResponse<Unit>
}
