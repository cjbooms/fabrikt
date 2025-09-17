package examples.pathLevelParameters.client

import feign.HeaderMap
import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine
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
    @RequestLine("GET /example?a={a}&b={b}")
    @Headers("X-Json-Encoded-Header: {xJsonEncodedHeader}")
    public fun getExample(
        @Param("a") a: String,
        @Param("b") b: String,
        @Param("xJsonEncodedHeader") xJsonEncodedHeader: String? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )
}
