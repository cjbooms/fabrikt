package examples.okHttpClient.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.okHttpClient.models.Content
import examples.okHttpClient.models.FirstModel
import examples.okHttpClient.models.QueryResult
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
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
    fun getExamplePath1(explodeListQueryParam: List<String>?, queryParam2: Int?):
        ApiResponse<QueryResult?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-1"
            .toHttpUrl()
            .newBuilder()
            .queryParam("explode_list_query_param", explodeListQueryParam, true)
            .queryParam("query_param2", queryParam2)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("Accept", "application/vnd.custom.media+json")
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     * POST example path 1
     *
     * @param explodeListQueryParam
     */
    @Throws(ApiException::class)
    fun postExamplePath1(generatedType: Content, explodeListQueryParam: List<String>?):
        ApiResponse<Unit?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-1"
            .toHttpUrl()
            .newBuilder()
            .queryParam("explode_list_query_param", explodeListQueryParam, true)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .post(objectMapper.writeValueAsString(generatedType).toRequestBody("application/json".toMediaType()))
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     * GET example path 2
     *
     * @param pathParam The resource id
     * @param queryParam2
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @Throws(ApiException::class)
    fun getExamplePath2PathParam(
        pathParam: String,
        queryParam2: Int?,
        ifNoneMatch: String?
    ): ApiResponse<Content?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-2/{path_param}"
            .pathParam("{path_param}" to pathParam)
            .toHttpUrl()
            .newBuilder()
            .queryParam("query_param2", queryParam2)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("If-None-Match", ifNoneMatch)
            .header("Accept", "application/json")
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     * HEAD example path 2
     *
     * @param pathParam The resource id
     * @param queryParam3
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @Throws(ApiException::class)
    fun headExamplePath2PathParam(
        pathParam: String,
        queryParam3: Boolean?,
        ifNoneMatch: String?
    ): ApiResponse<Unit?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-2/{path_param}"
            .pathParam("{path_param}" to pathParam)
            .toHttpUrl()
            .newBuilder()
            .queryParam("query_param3", queryParam3)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("If-None-Match", ifNoneMatch)
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .head()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }

    /**
     * PUT example path 2
     *
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @Throws(ApiException::class)
    fun putExamplePath2PathParam(
        firstModel: FirstModel,
        pathParam: String,
        ifMatch: String
    ): ApiResponse<Unit?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-2/{path_param}"
            .pathParam("{path_param}" to pathParam)
            .toHttpUrl()
            .newBuilder()
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("If-Match", ifMatch)
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .put(objectMapper.writeValueAsString(firstModel).toRequestBody("application/json".toMediaType()))
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}

@Suppress("unused")
class ExamplePathSubresourceClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient
) {
    /**
     * PUT example path 3
     *
     * @param csvListQueryParam
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @Throws(ApiException::class)
    fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        csvListQueryParam: List<String>?,
        pathParam: String,
        ifMatch: String
    ): ApiResponse<Unit?> {
        val httpUrl: HttpUrl = "$baseUrl/example-path-3/{path_param}/subresource"
            .pathParam("{path_param}" to pathParam)
            .toHttpUrl()
            .newBuilder()
            .queryParam("csv_list_query_param", csvListQueryParam, false)
            .build()

        val httpHeaders: Headers = Headers.Builder()
            .header("If-Match", ifMatch)
            .build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .put(objectMapper.writeValueAsString(firstModel).toRequestBody("application/json".toMediaType()))
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
