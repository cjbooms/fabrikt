package examples.okHttpClientPostWithoutRequestBody.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
public class ExampleClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     *
     */
    @Throws(ApiException::class)
    public fun putExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> {
        val httpUrl: HttpUrl = "$baseUrl/example"
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .put(ByteArray(0).toRequestBody())
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     *
     */
    @Throws(ApiException::class)
    public fun postExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> {
        val httpUrl: HttpUrl = "$baseUrl/example"
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .post(ByteArray(0).toRequestBody())
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     *
     */
    @Throws(ApiException::class)
    public fun patchExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit> {
        val httpUrl: HttpUrl = "$baseUrl/example"
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .patch(ByteArray(0).toRequestBody())
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
