package examples.okHttpClientMultiMediaType.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.okHttpClientMultiMediaType.models.QueryResult
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
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
        acceptHeader: String = "application/vnd.custom.media+xml"
    ): ApiResponse<QueryResult?> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.getExamplePath1(explodeListQueryParam, queryParam2, acceptHeader)
        }
}
