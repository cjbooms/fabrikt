package examples.operationsSchema.controllers

import examples.operationsSchema.models.ArrayPostRequestBody
import examples.operationsSchema.models.EnumPostParametersP
import examples.operationsSchema.models.EnumPostRequestBody
import examples.operationsSchema.models.ObjectPostParametersP
import examples.operationsSchema.models.ObjectPostRequestBody
import examples.operationsSchema.models.SimpleParametersPathP
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.QueryValue
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

@Controller
interface SimpleController {
    /**
     *
     *
     * @param simplePostRequestBody
     * @param pathP
     * @param p
     */
    @Post(uri = "/simple")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        simplePostRequestBody: String,
        @Valid @QueryValue(value = "path_p")
        pathP: SimpleParametersPathP?,
        @QueryValue(value = "p") p: Int?
    ): HttpResponse<Unit>
}

@Controller
interface EnumController {
    /**
     *
     *
     * @param enumPostRequestBody
     * @param p
     */
    @Post(uri = "/enum")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        enumPostRequestBody: EnumPostRequestBody,
        @QueryValue(value = "p")
        p: EnumPostParametersP?
    ): HttpResponse<Unit>
}

@Controller
interface ObjectController {
    /**
     *
     *
     * @param objectPostRequestBody
     * @param p
     */
    @Post(uri = "/object")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        objectPostRequestBody: ObjectPostRequestBody,
        @Valid @QueryValue(
            value =
            "p"
        )
        p: ObjectPostParametersP?
    ): HttpResponse<Unit>
}

@Controller
interface ArrayController {
    /**
     *
     *
     * @param arrayPostRequestBody
     * @param p
     */
    @Post(uri = "/array")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        arrayPostRequestBody: List<ArrayPostRequestBody>,
        @Valid @QueryValue(
            value =
            "p"
        )
        p: List<Int>?
    ): HttpResponse<Unit>
}
