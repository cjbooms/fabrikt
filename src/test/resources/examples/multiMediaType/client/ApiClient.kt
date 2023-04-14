package examples.multiMediaType.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.multiMediaType.models.ContentType
import examples.multiMediaType.models.QueryResult
import examples.multiMediaType.models.SuccessResponse
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
class ExamplePath1Client(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param acceptHeader
     */
    @Throws(ApiException::class)
    fun getExamplePath1(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
        acceptHeader: String = "application/vnd.custom.media+xml",
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<QueryResult> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-1"
            .toHttpUrl()
            .newBuilder()
            .queryParam("explode_list_query_param", explodeListQueryParam, true)
            .queryParam("query_param2", queryParam2)
            .build()

        val headerBuilder = Headers.Builder()
            .header("Accept", acceptHeader)
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}

@Suppress("unused")
class ExamplePath2Client(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param accept the content type accepted by the client
     */
    @Throws(ApiException::class)
    fun getExamplePath2(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
        accept: ContentType? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<QueryResult> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-2"
            .toHttpUrl()
            .newBuilder()
            .queryParam("explode_list_query_param", explodeListQueryParam, true)
            .queryParam("query_param2", queryParam2)
            .build()

        val headerBuilder = Headers.Builder()
            .header("Accept", accept?.value)
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}

@Suppress("unused")
class MultipleResponseSchemasClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     * GET with multiple response content schemas
     *
     * @param accept the content type accepted by the client
     */
    @Throws(ApiException::class)
    fun getMultipleResponseSchemas(
        accept: ContentType? = null,
        additionalHeaders: Map<String, String> =
            emptyMap(),
    ): ApiResponse<JsonNode> {
        val httpUrl: HttpUrl = "$baseUrl/multiple-response-schemas"
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
            .header("Accept", accept?.value)
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}

@Suppress("unused")
class DifferentSuccessAndErrorResponseSchemaClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient
) {
    /**
     *
     */
    @Throws(ApiException::class)
    fun getDifferentSuccessAndErrorResponseSchema(
        additionalHeaders: Map<String, String> =
            emptyMap()
    ): ApiResponse<SuccessResponse> {
        val httpUrl: HttpUrl = "$baseUrl/different-success-and-error-response-schema"
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
