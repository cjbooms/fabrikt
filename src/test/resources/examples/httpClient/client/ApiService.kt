package examples.httpClient.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.httpClient.models.Content
import examples.httpClient.models.FirstModel
import examples.httpClient.models.QueryResult
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.Throws

/**
 * The circuit breaker registry should have the proper configuration to correctly action on circuit
 * breaker transitions based on the client exceptions [ApiClientException], [ApiServerException] and
 * [IOException].
 *
 * @see ApiClientException
 * @see ApiServerException
 */
@Suppress("unused")
public class ExamplePath1Service(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "examplePath1Client"

    private val apiClient: ExamplePath1Client = ExamplePath1Client(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    public fun getExamplePath1(
        explodeListQueryParam: List<String>? = null,
        queryParam2: Int? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<QueryResult> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath1(explodeListQueryParam, queryParam2, additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun postExamplePath1(
        content: Content,
        explodeListQueryParam: List<String>? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.postExamplePath1(content, explodeListQueryParam, additionalHeaders)
        }
}

/**
 * The circuit breaker registry should have the proper configuration to correctly action on circuit
 * breaker transitions based on the client exceptions [ApiClientException], [ApiServerException] and
 * [IOException].
 *
 * @see ApiClientException
 * @see ApiServerException
 */
@Suppress("unused")
public class ExamplePath2Service(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "examplePath2Client"

    private val apiClient: ExamplePath2Client = ExamplePath2Client(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    public fun getExamplePath2PathParam(
        pathParam: String,
        limit: Int = 500,
        queryParam2: Int? = null,
        ifNoneMatch: String? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Content> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath2PathParam(pathParam, limit, queryParam2, ifNoneMatch, additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun headOperationIdExample(
        pathParam: String,
        queryParam3: Boolean? = null,
        ifNoneMatch: String? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.headOperationIdExample(pathParam, queryParam3, ifNoneMatch, additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun putExamplePath2PathParam(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.putExamplePath2PathParam(firstModel, pathParam, ifMatch, additionalHeaders)
        }
}

/**
 * The circuit breaker registry should have the proper configuration to correctly action on circuit
 * breaker transitions based on the client exceptions [ApiClientException], [ApiServerException] and
 * [IOException].
 *
 * @see ApiClientException
 * @see ApiServerException
 */
@Suppress("unused")
public class ExamplePath3SubresourceService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "examplePath3SubresourceClient"

    private val apiClient: ExamplePath3SubresourceClient = ExamplePath3SubresourceClient(
        objectMapper,
        baseUrl,
        client,
    )

    @Throws(ApiException::class)
    public fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        csvListQueryParam: List<String>? = null,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.putExamplePath3PathParamSubresource(firstModel, pathParam, ifMatch, csvListQueryParam, additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun deleteExamplePath3PathParamSubresource(
        additionalHeaders: Map<String, String> =
            emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.deleteExamplePath3PathParamSubresource(additionalHeaders)
        }
}

/**
 * The circuit breaker registry should have the proper configuration to correctly action on circuit
 * breaker transitions based on the client exceptions [ApiClientException], [ApiServerException] and
 * [IOException].
 *
 * @see ApiClientException
 * @see ApiServerException
 */
@Suppress("unused")
public class ExamplePath3SubresourceNameService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "examplePath3SubresourceNameClient"

    private val apiClient: ExamplePath3SubresourceNameClient =
        ExamplePath3SubresourceNameClient(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    public fun patchExamplePath3PathParamSubresourceName(
        firstModelPatch: Any,
        pathParam: String,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.patchExamplePath3PathParamSubresourceName(firstModelPatch, pathParam, additionalHeaders)
        }
}
