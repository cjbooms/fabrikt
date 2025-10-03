package examples.byteArrayStream.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import okhttp3.OkHttpClient
import kotlin.ByteArray
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
public class BinaryDataService(
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    objectMapper: ObjectMapper,
    baseUrl: String,
    okHttpClient: OkHttpClient,
) {
    public var circuitBreakerName: String = "binaryDataClient"

    private val apiClient: BinaryDataClient = BinaryDataClient(objectMapper, baseUrl, okHttpClient)

    @Throws(ApiException::class)
    public fun postBinaryData(
        applicationOctetStream: ByteArray,
        additionalHeaders: Map<String, String> = emptyMap(),
    ): ApiResponse<ByteArray> =
        withCircuitBreaker(circuitBreakerRegistry, circuitBreakerName) {
            apiClient.postBinaryData(applicationOctetStream, additionalHeaders)
        }
}
