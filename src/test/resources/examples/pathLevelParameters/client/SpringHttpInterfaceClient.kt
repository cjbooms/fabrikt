package examples.pathLevelParameters.client

import org.springframework.web.bind.`annotation`.RequestHeader
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.service.`annotation`.HttpExchange
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

@Suppress("unused")
public interface ExampleClient {
    /**
     *
     *
     * @param a
     * @param b
     * @param xJsonEncodedHeader Json Encoded header
     */
    @HttpExchange(
        url = "/example",
        method = "GET",
    )
    public fun getExample(
        @RequestParam("a") a: String,
        @RequestParam("b") b: String,
        @RequestHeader("X-Json-Encoded-Header") xJsonEncodedHeader: String? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    )
}
