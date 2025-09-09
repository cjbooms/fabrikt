package examples.externalReferences.aggressive.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.externalReferences.aggressive.models.ContainingExternalReference
import examples.externalReferences.aggressive.models.ExternalParameter
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
    okHttpClient: OkHttpClient,
) {
    public var circuitBreakerName: String = "helloClient"

    private val apiClient: HelloClient = HelloClient(objectMapper, baseUrl, okHttpClient)

    @Throws(ApiException::class)
    public fun helloWorld(
        parameter: ExternalParameter,
        additionalHeaders: Map<String, String> =
            emptyMap(),
    ): ApiResponse<ContainingExternalReference> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.helloWorld(parameter, additionalHeaders)
        }
}
