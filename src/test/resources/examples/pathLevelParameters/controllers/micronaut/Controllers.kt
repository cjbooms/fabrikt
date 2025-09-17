package examples.pathLevelParameters.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.Header
import io.micronaut.http.`annotation`.QueryValue
import io.micronaut.security.rules.SecurityRule
import kotlin.Any
import kotlin.String
import kotlin.Unit

@Controller
public interface ExampleController {
    /**
     *
     *
     * @param a
     * @param b
     * @param xJsonEncodedHeader Json Encoded header
     */
    @Get(uri = "/example")
    public fun `get`(
        @QueryValue(value = "a") a: String,
        @QueryValue(value = "b") b: String,
        @Header(value = "X-Json-Encoded-Header") xJsonEncodedHeader: Any?,
    ): HttpResponse<Unit>
}