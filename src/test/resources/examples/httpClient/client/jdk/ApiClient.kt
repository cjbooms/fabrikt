package examples.httpClient.jdk.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.httpClient.jdk.models.Content
import examples.httpClient.jdk.models.FirstModel
import examples.httpClient.jdk.models.QueryResult
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
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
     */
    @Throws(ApiException::class)
    public fun getExamplePath1(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
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
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }

    /**
     * POST example path 1
     *
     * @param content
     * @param explodeListQueryParam
     */
    @Throws(ApiException::class)
    public fun postExamplePath1(
        content: Content,
        explodeListQueryParam: List<String>? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-1").addQueryParam(
            "explode_list_query_param",
            explodeListQueryParam,
            true,
        )
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .POST(Publishers.jsonBodyPublisher(content))
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
     * GET example path 2
     *
     * @param pathParam The resource id
     * @param limit
     * @param queryParam2
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @Throws(ApiException::class)
    public fun getExamplePath2PathParam(
        pathParam: String,
        limit: Int = 500,
        queryParam2: Int? = null,
        ifNoneMatch: String? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Content> {
        val url: Url = Url("$baseUrl/example-path-2/{path_param}")
            .addPathParam("path_param", pathParam)
            .addQueryParam("limit", limit)
            .addQueryParam("query_param2", queryParam2)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .GET()
        ifNoneMatch?.let { requestBuilder.header("If-None-Match", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }

    /**
     * HEAD example path 2
     *
     * @param pathParam The resource id
     * @param queryParam3
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @Throws(ApiException::class)
    public fun headOperationIdExample(
        pathParam: String,
        queryParam3: Boolean? = null,
        ifNoneMatch: String? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-2/{path_param}")
            .addPathParam("path_param", pathParam)
            .addQueryParam("query_param3", queryParam3)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .method("HEAD", BodyPublishers.noBody())
        ifNoneMatch?.let { requestBuilder.header("If-None-Match", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }

    /**
     * PUT example path 2
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @Throws(ApiException::class)
    public fun putExamplePath2PathParam(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-2/{path_param}")
            .addPathParam("path_param", pathParam)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .PUT(Publishers.jsonBodyPublisher(firstModel))
        ifMatch?.let { requestBuilder.header("If-Match", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}

@Suppress("unused")
public class ExamplePath3SubresourceClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     * PUT example path 3
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     * @param csvListQueryParam
     */
    @Throws(ApiException::class)
    public fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        csvListQueryParam: List<String>? = null,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-3/{path_param}/subresource")
            .addPathParam("path_param", pathParam).addQueryParam(
                "csv_list_query_param",
                csvListQueryParam,
                false,
            )
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .PUT(Publishers.jsonBodyPublisher(firstModel))
        ifMatch?.let { requestBuilder.header("If-Match", it.toString()) }
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }

    /**
     * DELETE example path 3
     */
    @Throws(ApiException::class)
    public fun deleteExamplePath3PathParamSubresource(
        additionalHeaders: Map<String, String?> =
            emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-3/{path_param}/subresource")
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .DELETE()
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}

@Suppress("unused")
public class ExamplePath3SubresourceNameClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: HttpClient,
) {
    /**
     * PATCH example path 3
     *
     * @param firstModelPatch
     * @param pathParam The resource id
     */
    @Throws(ApiException::class)
    public fun patchExamplePath3PathParamSubresourceName(
        firstModelPatch: Any,
        pathParam: String,
        additionalHeaders: Map<String, String?> = emptyMap(),
    ): ApiResponse<Unit> {
        val url: Url = Url("$baseUrl/example-path-3/{path_param}/subresource/name")
            .addPathParam("path_param", pathParam)
        val requestBuilder: HttpRequest.Builder = HttpRequest.newBuilder()
            .uri(url.toUri())
            .version(HttpClient.Version.HTTP_1_1)
            .method("PATCH", Publishers.jsonBodyPublisher(firstModelPatch))
        additionalHeaders.forEach { requestBuilder.header(it.key, it.value) }
        return client.execute(requestBuilder.build())
    }
}
