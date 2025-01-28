package examples.queryParameters.client

import feign.HeaderMap
import feign.Param
import feign.QueryMap
import feign.RequestLine
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
     * @param inlineEnum
     */
    @RequestLine("GET /example?a={a}&b={b}&inline_enum.={inlineEnum}")
    public fun getExample(
        @Param("a") a: String,
        @Param("b") b: String,
        @Param("inlineEnum") inlineEnum: Any? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )
}
