package examples.externalReferences.client

import com.fasterxml.jackson.databind.ObjectMapper
import examples.externalReferences.models.Content
import examples.externalReferences.models.Language
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.String
import kotlin.Suppress
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
public class HelloService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    client: OkHttpClient,
) {
    public var circuitBreakerName: String = "helloClient"

    private val apiClient: HelloClient = HelloClient(objectMapper, baseUrl, client)

    @Throws(ApiException::class)
    public fun helloWorld(language: Language, additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Content> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.helloWorld(language, additionalHeaders)
        }
}
