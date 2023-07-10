package examples.differentErrorResponseBody.client

import examples.differentErrorResponseBody.models.SuccessResponse
import feign.HeaderMap
import feign.Headers
import feign.RequestLine
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map

@Suppress("unused")
interface ExampleClient {
    /**
     *
     */
    @RequestLine("GET /example")
    @Headers("Accept: application/json")
    fun getExample(@HeaderMap additionalHeaders: Map<String, String> = emptyMap()): SuccessResponse
}
