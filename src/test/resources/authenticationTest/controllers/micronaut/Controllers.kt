package authenticationTest.controllers

import authenticationTest.models.TestDTO
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import javax.validation.Valid
import kotlin.Unit

@Controller
interface TestController {
    /**
     *
     *
     * @param testDTO
     */
    @Get(uri = "/test")
    fun testPath(
        @Valid @QueryValue(value = "testDTO")
        testDTO: TestDTO
    ): HttpResponse<Unit>
}
