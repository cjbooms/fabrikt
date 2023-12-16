package examples.multiMediaType.jdk.client

import java.net.http.HttpHeaders

/**
 * API 2xx success response returned by API call.
 *
 * @param <T> The type of data that is deserialized from response body
 */
data class ApiResponse<T>(val statusCode: Int, val headers: HttpHeaders, val data: T? = null)

/**
 * API non-2xx failure responses returned by API call.
 */
open class ApiException(override val message: String) : RuntimeException(message)

/**
 * API 3xx redirect response returned by API call.
 */
open class ApiRedirectException(val statusCode: Int, val headers: HttpHeaders, override val message: String) : ApiException(message)

/**
 * API 4xx failure responses returned by API call.
 */
data class ApiClientException(val statusCode: Int, val headers: HttpHeaders, override val message: String) :
    ApiException(message)

/**
 * API 5xx failure responses returned by API call.
 */
data class ApiServerException(val statusCode: Int, val headers: HttpHeaders, override val message: String) :
    ApiException(message)
