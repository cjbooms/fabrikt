package examples.okHttpClientPostWithoutRequestBody.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
class ExampleClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient
) {
    /**
     *
     */
    @Throws(ApiException::class)
    fun postExample(additionalHeaders: Map<String, String> = emptyMap()): ApiResponse<Unit?> {
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
            .post()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
