package examples.queryParameters.controllers

import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.QueryValue
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
     * @param inlineEnum
     */
    @Get(uri = "/example")
    public fun `get`(
        @QueryValue(value = "a") a: String,
        @QueryValue(value = "b") b: String,
        @QueryValue(value = "inline_enum.") inlineEnum: Any?,
    ): HttpResponse<Unit>
}
