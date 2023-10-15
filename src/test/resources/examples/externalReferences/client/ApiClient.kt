package examples.externalReferences.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.externalReferences.models.ExternalParameter
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
public class HelloClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     *
     *
     * @param language
     */
    @Throws(ApiException::class)
    public fun helloWorld(
        language: ExternalParameter,
        additionalHeaders: Map<String, String> =
            emptyMap(),
    ): ApiResponse<Any> {
        val httpUrl: HttpUrl = "$baseUrl/hello"
            .pathParam("{language}" to language)
            .toHttpUrl()
            .newBuilder()
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
