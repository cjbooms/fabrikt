package examples.okHttpClient.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.okHttpClient.models.Content
import examples.okHttpClient.models.FirstModel
import examples.okHttpClient.models.QueryResult
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
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
class ExamplePath1Service(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient
) {
    var circuitBreakerName: String = "examplePath1Client"

    private val apiClient: ExamplePath1Client = ExamplePath1Client(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    fun getExamplePath1(
        explodeListQueryParam: List<String>?,
        queryParam2: Int?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<QueryResult?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath1(explodeListQueryParam, queryParam2, additionalHeaders)
        }

    @Throws(ApiException::class)
    fun postExamplePath1(
        content: Content,
        explodeListQueryParam: List<String>?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
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
class ExamplePath2Service(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient
) {
    var circuitBreakerName: String = "examplePath2Client"

    private val apiClient: ExamplePath2Client = ExamplePath2Client(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    fun getExamplePath2PathParam(
        pathParam: String,
        queryParam2: Int?,
        ifNoneMatch: String?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Content?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath2PathParam(pathParam, queryParam2, ifNoneMatch, additionalHeaders)
        }

    @Throws(ApiException::class)
    fun headOperationIdExample(
        pathParam: String,
        queryParam3: Boolean?,
        ifNoneMatch: String?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.headOperationIdExample(pathParam, queryParam3, ifNoneMatch, additionalHeaders)
        }

    @Throws(ApiException::class)
    fun putExamplePath2PathParam(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
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
class ExamplePath3SubresourceService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient
) {
    var circuitBreakerName: String = "examplePath3SubresourceClient"

    private val apiClient: ExamplePath3SubresourceClient = ExamplePath3SubresourceClient(
        objectMapper,
        baseUrl,
        client
    )

    @Throws(ApiException::class)
    fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String,
        csvListQueryParam: List<String>?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.putExamplePath3PathParamSubresource(firstModel, pathParam, ifMatch, csvListQueryParam, additionalHeaders)
        }
}
