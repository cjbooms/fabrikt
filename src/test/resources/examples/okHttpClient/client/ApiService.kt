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
class ExamplePathService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient
) {
    var circuitBreakerName: String = "examplePathClient"

    private val apiClient: ExamplePathClient = ExamplePathClient(objectMapper, baseUrl, client)

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
        generatedType: Content,
        explodeListQueryParam: List<String>?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.postExamplePath1(generatedType, explodeListQueryParam, additionalHeaders)
        }

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
    fun headExamplePath2PathParam(
        pathParam: String,
        queryParam3: Boolean?,
        ifNoneMatch: String?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.headExamplePath2PathParam(pathParam, queryParam3, ifNoneMatch, additionalHeaders)
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
class ExamplePathSubresourceService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient
) {
    var circuitBreakerName: String = "examplePathSubresourceClient"

    private val apiClient: ExamplePathSubresourceClient = ExamplePathSubresourceClient(
        objectMapper,
        baseUrl,
        client
    )

    @Throws(ApiException::class)
    fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        csvListQueryParam: List<String>?,
        pathParam: String,
        ifMatch: String,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<Unit?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.putExamplePath3PathParamSubresource(firstModel, csvListQueryParam, pathParam, ifMatch, additionalHeaders)
        }
}
