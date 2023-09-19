package examples.okHttpClientPostWithoutRequestBody.client

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
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
public class ExampleService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "exampleClient"

    private val apiClient: ExampleClient = ExampleClient(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    public fun putExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.putExample(additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun postExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.postExample(additionalHeaders)
        }

    @Throws(ApiException::class)
    public fun patchExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.patchExample(additionalHeaders)
        }
}
