package examples.multiMediaType.client

import com.fasterxml.jackson.databind.JsonNode
import examples.multiMediaType.models.ContentType
import examples.multiMediaType.models.QueryResult
import examples.multiMediaType.models.SuccessResponse
import feign.HeaderMap
import feign.Headers
import feign.Param
import feign.RequestLine
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

@Suppress("unused")
interface ExamplePath1Client {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param acceptHeader
     */
    @RequestLine("GET /example-path-1?explode_list_query_param={explodeListQueryParam}&query_param2={queryParam2}")
    @Headers(value = ["Accept: {acceptHeader}", "Accept: application/vnd.custom.media+xml"])
    fun getExamplePath1(
        @Param("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @Param("queryParam2") queryParam2: Int? = null,
        @Param("acceptHeader") acceptHeader: String = "application/vnd.custom.media+xml",
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap()
    ): QueryResult
}

@Suppress("unused")
interface ExamplePath2Client {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param accept the content type accepted by the client
     */
    @RequestLine("GET /example-path-2?explode_list_query_param={explodeListQueryParam}&query_param2={queryParam2}")
    @Headers(value = ["Accept: {accept}", "Accept: application/vnd.custom.media+xml"])
    fun getExamplePath2(
        @Param("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @Param("queryParam2") queryParam2: Int? = null,
        @Param("accept") accept: ContentType? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap()
    ): QueryResult
}

@Suppress("unused")
interface MultipleResponseSchemasClient {
    /**
     * GET with multiple response content schemas
     *
     * @param accept the content type accepted by the client
     */
    @RequestLine("GET /multiple-response-schemas")
    @Headers(value = ["Accept: {accept}", "Accept: application/json"])
    fun getMultipleResponseSchemas(
        @Param("accept") accept: ContentType? = null,
        @HeaderMap
        additionalHeaders: Map<String, String> = emptyMap()
    ): JsonNode
}

@Suppress("unused")
interface DifferentSuccessAndErrorResponseSchemaClient {
    /**
     *
     */
    @RequestLine("GET /different-success-and-error-response-schema")
    @Headers("Accept: application/json")
    fun getDifferentSuccessAndErrorResponseSchema(
        @HeaderMap additionalHeaders: Map<String, String> =
            emptyMap()
    ): SuccessResponse
}
