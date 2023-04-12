package examples.authentication.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.QueryValue
import kotlin.String
import kotlin.Unit

@Controller
interface RequiredController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/required")
    fun testPath(@QueryValue(value = "testString") testString: String): HttpResponse<Unit>
}

@Controller
interface ProhibitedController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/prohibited")
    fun testPath(@QueryValue(value = "testString") testString: String): HttpResponse<Unit>
}

@Controller
interface OptionalController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/optional")
    fun testPath(@QueryValue(value = "testString") testString: String): HttpResponse<Unit>
}

@Controller
interface NoneController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/none")
    fun testPath(@QueryValue(value = "testString") testString: String): HttpResponse<Unit>
}

@Controller
interface DefaultController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/default")
    fun testPath(@QueryValue(value = "testString") testString: String): HttpResponse<Unit>
}
