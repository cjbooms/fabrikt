package examples.externalReferences.aggressive.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.externalReferences.aggressive.models.ContainingExternalReference
import examples.externalReferences.aggressive.models.ExternalParameter
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.String
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
public class HelloClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val okHttpClient: OkHttpClient,
) {
    /**
     *
     *
     * @param parameter
     */
    @Throws(ApiException::class)
    public fun helloWorld(
        parameter: ExternalParameter,
        additionalHeaders: Map<String, String> = emptyMap(),
        additionalQueryParameters: Map<String, String> = emptyMap(),
    ): ApiResponse<ContainingExternalReference> {
        val httpUrl: HttpUrl =
            "$baseUrl/hello"
                .pathParam("{parameter}" to parameter)
                .toHttpUrl()
                .newBuilder()
                .also { builder -> additionalQueryParameters.forEach { builder.queryParam(it.key, it.value) } }
                .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request =
            Request
                .Builder()
                .url(httpUrl)
                .headers(httpHeaders)
                .get()
                .build()

        return request.execute(okHttpClient, objectMapper, jacksonTypeRef())
    }
}
