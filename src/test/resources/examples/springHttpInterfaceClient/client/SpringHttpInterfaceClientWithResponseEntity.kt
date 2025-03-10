package examples.springHttpInterfaceClient.client

import examples.springHttpInterfaceClient.models.Content
import examples.springHttpInterfaceClient.models.FirstModel
import examples.springHttpInterfaceClient.models.QueryResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RequestHeader
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.service.`annotation`.HttpExchange
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map

@Suppress("unused")
public interface ExamplePath1Client {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param headerParam1
     * @param headerParam2
     */
    @HttpExchange(
        url = "/example-path-1",
        method = "GET",
        accept = ["application/vnd.custom.media+json"],
    )
    public fun getExamplePath1(
        @RequestParam("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @RequestParam("queryParam2") queryParam2: Int? = null,
        @RequestHeader("headerParam1") headerParam1: String? = null,
        @RequestHeader("headerParam2") headerParam2: String? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<QueryResult>

    /**
     * POST example path 1
     *
     * @param content
     * @param explodeListQueryParam
     */
    @HttpExchange(
        url = "/example-path-1",
        method = "POST",
    )
    public fun postExamplePath1(
        @RequestBody content: Content,
        @RequestParam("explodeListQueryParam") explodeListQueryParam: List<String>? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<Unit>
}

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
    @HttpExchange(
        url = "/example-path-2/{pathParam}",
        method = "GET",
        accept = ["application/json"],
    )
    public fun getExamplePath2PathParam(
        @PathVariable("pathParam") pathParam: String,
        @RequestParam("limit") limit: Int = 500,
        @RequestParam("queryParam2") queryParam2: Int? = null,
        @RequestHeader("ifNoneMatch") ifNoneMatch: String? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<Content>

    /**
     * HEAD example path 2
     *
     * @param pathParam The resource id
     * @param queryParam3
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @HttpExchange(
        url = "/example-path-2/{pathParam}",
        method = "HEAD",
    )
    public fun headOperationIdExample(
        @PathVariable("pathParam") pathParam: String,
        @RequestParam("queryParam3") queryParam3: Boolean? = null,
        @RequestHeader("ifNoneMatch") ifNoneMatch: String? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<Unit>

    /**
     * PUT example path 2
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @HttpExchange(
        url = "/example-path-2/{pathParam}",
        method = "PUT",
    )
    public fun putExamplePath2PathParam(
        @RequestBody firstModel: FirstModel,
        @PathVariable("pathParam") pathParam: String,
        @RequestHeader("ifMatch") ifMatch: String,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<Unit>
}

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
    @HttpExchange(
        url = "/example-path-3/{pathParam}/subresource",
        method = "PUT",
    )
    public fun putExamplePath3PathParamSubresource(
        @RequestBody firstModel: FirstModel,
        @PathVariable("pathParam") pathParam: String,
        @RequestHeader("ifMatch") ifMatch: String,
        @RequestParam("csvListQueryParam") csvListQueryParam: List<String>? = null,
        @RequestHeader additionalHeaders: Map<String, Any> = emptyMap(),
        @RequestParam additionalQueryParameters: Map<String, Any> = emptyMap(),
    ): ResponseEntity<Unit>
}
