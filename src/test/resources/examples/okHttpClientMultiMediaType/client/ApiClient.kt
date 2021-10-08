package examples.okHttpClientMultiMediaType.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.okHttpClientMultiMediaType.models.QueryResult
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.Throws

@Suppress("unused")
class ExamplePathClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient
) {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     */
    @Throws(ApiException::class)
    fun getExamplePath1(
        explodeListQueryParam: List<String>?,
        queryParam2: Int?,
        acceptHeader: String = "application/vnd.custom.media+xml"
    ): ApiResponse<QueryResult?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-1"
            .toHttpUrl()
            .newBuilder()
            .queryParam("explode_list_query_param", explodeListQueryParam, true)
            .queryParam("query_param2", queryParam2)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("Accept", acceptHeader)
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
