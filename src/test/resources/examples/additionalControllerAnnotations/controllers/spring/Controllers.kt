package ie.zalando.controllers

import example.AdditionalAnnotationPerOperation
import example.Annotation1
import example.Annotation2
import example.MethodAnnotation1
import example.MethodAnnotation2
import ie.zalando.models.Content
import ie.zalando.models.FirstModel
import ie.zalando.models.QueryResult
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.`annotation`.Validated
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RequestHeader
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RequestMethod
import org.springframework.web.bind.`annotation`.RequestParam
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

@Controller
@Validated
@RequestMapping("")
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
    @RequestMapping(
        value = ["/example-path-1"],
        produces = ["application/vnd.custom.media+json"],
        method = [RequestMethod.GET],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun `get`(
        @Valid @RequestParam(value = "explode_list_query_param", required = false)
        explodeListQueryParam: List<String>?,
        @RequestParam(value = "query_param2", required = false) queryParam2: Int?,
        @Valid @RequestParam(value = "int_list_query_param", required = false)
        intListQueryParam: List<Int>?,
    ): ResponseEntity<QueryResult>

    /**
     * POST example path 1
     *
     * @param content
     * @param explodeListQueryParam
     */
    @RequestMapping(
        value = ["/example-path-1"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun post(
        @RequestBody @Valid content: Content,
        @Valid @RequestParam(
            value =
            "explode_list_query_param",
            required = false,
        ) explodeListQueryParam: List<String>?,
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
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
    @RequestMapping(
        value = ["/example-path-2/{path_param}"],
        produces = [
            "application/json", "application/json", "application/json", "application/json",
            "application/json",
        ],
        method = [RequestMethod.GET],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun getById(
        @PathVariable(value = "path_param", required = true) pathParam: String,
        @Min(1) @Max(1_000) @RequestParam(value = "limit", required = false, defaultValue = "500")
        limit: Int,
        @RequestParam(value = "query_param2", required = false) queryParam2: Int?,
        @RequestHeader(value = "If-None-Match", required = false) ifNoneMatch: String?,
    ): ResponseEntity<Content>

    /**
     * PUT example path 2
     *
     * @param firstModel
     * @param pathParam The resource id
     * @param ifMatch The RFC7232 If-Match header field
     */
    @RequestMapping(
        value = ["/example-path-2/{path_param}"],
        produces = [],
        method = [RequestMethod.PUT],
        consumes = ["application/json"],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    public fun putById(
        @RequestBody @Valid firstModel: FirstModel,
        @PathVariable(value = "path_param", required = true) pathParam: String,
        @RequestHeader(value = "If-Match", required = true) ifMatch: String,
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
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
    @RequestMapping(
        value = ["/example-path-3/{path_param}/subresource"],
        produces = [],
        method = [RequestMethod.PUT],
        consumes = ["application/json"],
    )
    @MethodAnnotation1
    @MethodAnnotation2
    @AdditionalAnnotationPerOperation
    public fun put(
        @RequestBody @Valid firstModel: FirstModel,
        @PathVariable(value = "path_param", required = true) pathParam: String,
        @RequestHeader(value = "If-Match", required = true) ifMatch: String,
        @Valid @RequestParam(value = "csv_list_query_param", required = false)
        csvListQueryParam: List<String>?,
    ): ResponseEntity<Unit>
}
