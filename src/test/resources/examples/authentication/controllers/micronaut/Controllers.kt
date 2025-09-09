package examples.authentication.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.QueryValue
import io.micronaut.security.`annotation`.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import kotlin.String
import kotlin.Unit

@Controller
public interface RequiredController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/required")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public fun testPath(
        @QueryValue(value = "testString") testString: String,
        authentication: Authentication,
    ): HttpResponse<Unit>
}

@Controller
public interface ProhibitedController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/prohibited")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public fun testPath(
        @QueryValue(value = "testString") testString: String,
    ): HttpResponse<Unit>
}

@Controller
public interface OptionalController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/optional")
    @Secured(SecurityRule.IS_AUTHENTICATED, SecurityRule.IS_ANONYMOUS)
    public fun testPath(
        @QueryValue(value = "testString") testString: String,
        authentication: Authentication?,
    ): HttpResponse<Unit>
}

@Controller
public interface NoneController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/none")
    public fun testPath(
        @QueryValue(value = "testString") testString: String,
    ): HttpResponse<Unit>
}

@Controller
public interface DefaultController {
    /**
     *
     *
     * @param testString
     */
    @Get(uri = "/default")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public fun testPath(
        @QueryValue(value = "testString") testString: String,
        authentication: Authentication,
    ): HttpResponse<Unit>
}