package example.additionalannotation.client

import example.AdditionalAnnotationPerOperation
import example.Annotation1
import example.Annotation2
import example.MethodAnnotation1
import example.MethodAnnotation2
import example.additionalannotation.models.Content
import example.additionalannotation.models.FirstModel
import example.additionalannotation.models.QueryResult
import feign.HeaderMap
import feign.Headers
import feign.Param
import feign.QueryMap
import feign.RequestLine
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

@Annotation1
@Annotation2
@Suppress("unused")
public interface ExamplePath1Client {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param intListQueryParam
     */
    @RequestLine("GET /example-path-1?explode_list_query_param={explodeListQueryParam}&query_param2={queryParam2}&int_list_query_param={intListQueryParam}")
    @Headers("Accept: application/vnd.custom.media+json")
    @MethodAnnotation1
    @MethodAnnotation2
    public fun getExamplePath1(
        @Param("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @Param("queryParam2") queryParam2: Int? = null,
        @Param("intListQueryParam") intListQueryParam: List<Int>? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    ): QueryResult

    /**
     * POST example path 1
     *
     * @param content
     * @param explodeListQueryParam
     */
    @RequestLine("POST /example-path-1?explode_list_query_param={explodeListQueryParam}")
    @MethodAnnotation1
    @MethodAnnotation2
    public fun postExamplePath1(
        content: Content,
        @Param("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )
}

@Annotation1
@Annotation2
@Suppress("unused")
public interface ExamplePath2Client {
    /**
     * GET example path 2
     *
     * @param pathParam The resource id
     * @param limit
     * @param queryParam2
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @RequestLine("GET /example-path-2/{pathParam}?limit={limit}&query_param2={queryParam2}")
    @Headers(
        "If-None-Match: {ifNoneMatch}",
        "Accept: application/json",
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun getExamplePath2PathParam(
        @Param("pathParam") pathParam: String,
        @Param("limit") limit: Int = 500,
        @Param("queryParam2") queryParam2: Int? = null,
        @Param("ifNoneMatch") ifNoneMatch: String? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    ): Content

    /**
     * HEAD example path 2
     *
     * @param pathParam The resource id
     * @param queryParam3
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @RequestLine("HEAD /example-path-2/{pathParam}?query_param3={queryParam3}")
    @Headers("If-None-Match: {ifNoneMatch}")
    @MethodAnnotation1
    @MethodAnnotation2
    public fun headOperationIdExample(
        @Param("pathParam") pathParam: String,
        @Param("queryParam3") queryParam3: Boolean? = null,
        @Param("ifNoneMatch") ifNoneMatch: String? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )

    /**
     * PUT example path 2
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @RequestLine("PUT /example-path-2/{pathParam}")
    @Headers("If-Match: {ifMatch}")
    @MethodAnnotation1
    @MethodAnnotation2
    public fun putExamplePath2PathParam(
        firstModel: FirstModel,
        @Param("pathParam") pathParam: String,
        @Param("ifMatch") ifMatch: String,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )
}

@Annotation1
@Annotation2
@Suppress("unused")
public interface ExamplePath3SubresourceClient {
    /**
     * PUT example path 3
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     * @param csvListQueryParam
     */
    @RequestLine("PUT /example-path-3/{pathParam}/subresource?csv_list_query_param={csvListQueryParam}")
    @Headers("If-Match: {ifMatch}")
    @MethodAnnotation1
    @MethodAnnotation2
    @AdditionalAnnotationPerOperation
    public fun putExamplePath3PathParamSubresource(
        firstModel: FirstModel,
        @Param("pathParam") pathParam: String,
        @Param("ifMatch") ifMatch: String,
        @Param("csvListQueryParam") csvListQueryParam: List<String>? = null,
        @HeaderMap additionalHeaders: Map<String, String> = emptyMap(),
        @QueryMap additionalQueryParameters: Map<String, String> = emptyMap(),
    )
}
