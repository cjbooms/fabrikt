package examples.okHttpClientMultiMediaType.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.okHttpClientMultiMediaType.models.ContentType
import examples.okHttpClientMultiMediaType.models.QueryResult
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.Int
import kotlin.String
import kotlin.Suppress
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
        acceptHeader: String = "application/vnd.custom.media+xml",
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<QueryResult?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath1(explodeListQueryParam, queryParam2, acceptHeader, additionalHeaders)
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
    fun getExamplePath2(
        explodeListQueryParam: List<String>?,
        queryParam2: Int?,
        accept: ContentType?,
        additionalHeaders: Map<String, String> = emptyMap()
    ): ApiResponse<QueryResult?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath2(explodeListQueryParam, queryParam2, accept, additionalHeaders)
        }
}
