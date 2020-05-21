package examples.okHttpClient.client

import okhttp3.Headers

/**
 * API 2xx success response returned by API call.
 *
 * @param <T> The type of data that is deserialized from response body
 */
data class ApiResponse<T>(val statusCode: Int, val headers: Headers, val data: T? = null)

/**
 * API non-2xx failure responses returned by API call.
 */
data class ApiException(val statusCode: Int, val headers: Headers, override val message: String) : Exception(message)

