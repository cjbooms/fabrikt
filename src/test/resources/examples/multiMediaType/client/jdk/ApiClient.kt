package examples.multiMediaType.jdk.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import examples.multiMediaType.jdk.models.ContentType
import examples.multiMediaType.jdk.models.QueryResult
import examples.multiMediaType.jdk.models.SuccessResponse
import java.net.http.HttpClient
import java.net.http.HttpRequest
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
public class ExamplePath1Client(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param acceptHeader
     */
    @Throws(ApiException::class)
    public fun getExamplePath1(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
        acceptHeader: String = "application/vnd.custom.media+xml",
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<QueryResult> {
        val url: Url = Url("$baseUrl/example-path-1").addQueryParam(
            "explode_list_query_param",
            explodeListQueryParam,
            true,
        )
            .addQueryParam("query_param2", queryParam2)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .GET()
        acceptHeader?.let { requestBuilder.header("Accept", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}

@Suppress("unused")
public class ExamplePath2Client(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param accept the content type accepted by the client
     */
    @Throws(ApiException::class)
    public fun getExamplePath2(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
        accept: ContentType? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<QueryResult> {
        val url: Url = Url("$baseUrl/example-path-2").addQueryParam(
            "explode_list_query_param",
            explodeListQueryParam,
            true,
        )
            .addQueryParam("query_param2", queryParam2)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .GET()
        accept?.let { requestBuilder.header("Accept", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}

@Suppress("unused")
public class MultipleResponseSchemasClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     * GET with multiple response content schemas
     *
     * @param accept the content type accepted by the client
     */
    @Throws(ApiException::class)
    public fun getMultipleResponseSchemas(
        accept: ContentType? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<JsonNode> {
        val url: Url = Url("$baseUrl/multiple-response-schemas")
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .GET()
        accept?.let { requestBuilder.header("Accept", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}

@Suppress("unused")
public class DifferentSuccessAndErrorResponseSchemaClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     *
     */
    @Throws(ApiException::class)
    public fun getDifferentSuccessAndErrorResponseSchema(
        additionalHeaders: Map<String, String?> =
            emptyMap(),
    ): ApiResponse<SuccessResponse> {
        val url: Url = Url("$baseUrl/different-success-and-error-response-schema")
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .GET()
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}
