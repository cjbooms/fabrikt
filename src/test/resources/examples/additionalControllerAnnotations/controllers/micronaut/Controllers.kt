package ie.zalando.controllers

import example.AdditionalAnnotationPerOperation
import example.Annotation1
import example.Annotation2
import example.MethodAnnotation1
import example.MethodAnnotation2
import ie.zalando.models.Content
import ie.zalando.models.FirstModel
import ie.zalando.models.QueryResult
import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Body
import io.micronaut.http.`annotation`.Consumes
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.Header
import io.micronaut.http.`annotation`.PathVariable
import io.micronaut.http.`annotation`.Post
import io.micronaut.http.`annotation`.Produces
import io.micronaut.http.`annotation`.Put
import io.micronaut.http.`annotation`.QueryValue
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

@Controller
@Annotation1
@Annotation2
public interface ExamplePath1Controller {
    /**
     * GET example path 1
     *
     * @param explodeListQueryParam
     * @param queryParam2
     * @param intListQueryParam
     */
    @Get(uri = "/example-path-1")
    @Produces(value = ["application/vnd.custom.media+json"])
    @MethodAnnotation1
    @MethodAnnotation2
    public fun `get`(
        @Valid @QueryValue(value = "explode_list_query_param") explodeListQueryParam: List<String>?,
        @QueryValue(value = "query_param2") queryParam2: Int?,
        @Valid @QueryValue(value = "int_list_query_param") intListQueryParam: List<Int>?,
    ): HttpResponse<QueryResult>

    /**
     * POST example path 1
     *
     * @param content
     * @param explodeListQueryParam
     */
    @Post(uri = "/example-path-1")
    @Consumes(value = ["application/json"])
    @MethodAnnotation1
    @MethodAnnotation2
    public fun post(
        @Body @Valid content: Content,
        @Valid @QueryValue(
            value =
            "explode_list_query_param",
        ) explodeListQueryParam: List<String>?,
    ): HttpResponse<Unit>
}

@Controller
@Annotation1
@Annotation2
public interface ExamplePath2Controller {
    /**
     * GET example path 2
     *
     * @param pathParam The resource id
     * @param limit
     * @param queryParam2
     * @param ifNoneMatch The RFC7232 If-None-Match header field
     */
    @Get(uri = "/example-path-2/{path_param}")
    @Produces(
        value = [
            "application/json", "application/json", "application/json", "application/json",
            "application/json",
        ],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun getById(
        @PathVariable(value = "path_param") pathParam: String,
        @Min(1) @Max(1_000) @QueryValue(value = "limit", defaultValue = "500") limit: Int,
        @QueryValue(value = "query_param2") queryParam2: Int?,
        @Header(value = "If-None-Match") ifNoneMatch: String?,
    ): HttpResponse<Content>

    /**
     * PUT example path 2
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @Put(uri = "/example-path-2/{path_param}")
    @Consumes(value = ["application/json"])
    @MethodAnnotation1
    @MethodAnnotation2
    public fun putById(
        @Body @Valid firstModel: FirstModel,
        @PathVariable(value = "path_param") pathParam: String,
        @Header(value = "If-Match") ifMatch: String,
    ): HttpResponse<Unit>
}

@Controller
@Annotation1
@Annotation2
public interface ExamplePath3SubresourceController {
    /**
     * PUT example path 3
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     * @param csvListQueryParam
     */
    @Put(uri = "/example-path-3/{path_param}/subresource")
    @Consumes(value = ["application/json"])
    @MethodAnnotation1
    @MethodAnnotation2
    @AdditionalAnnotationPerOperation
    public fun put(
        @Body @Valid firstModel: FirstModel,
        @PathVariable(value = "path_param") pathParam: String,
        @Header(value = "If-Match") ifMatch: String,
        @Valid @QueryValue(value = "csv_list_query_param") csvListQueryParam: List<String>?,
    ): HttpResponse<Unit>
}
