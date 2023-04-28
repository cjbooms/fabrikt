package examples.operationsSchema.controllers

import examples.operationsSchema.models.ArrayPostApplicationJsonRequestBody
import examples.operationsSchema.models.EnumPostApplicationJsonRequestBody
import examples.operationsSchema.models.EnumPostPParameters
import examples.operationsSchema.models.ObjectPostApplicationJsonRequestBody
import examples.operationsSchema.models.ObjectPostPParameters
import examples.operationsSchema.models.SimplePathPParameters
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
     * @param requestBody
     * @param pathP
     * @param p
     */
    @Post(uri = "/simple")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        requestBody: String,
        @Valid @QueryValue(value = "path_p")
        pathP: SimplePathPParameters?,
        @QueryValue(value = "p") p: Int?
    ): HttpResponse<Unit>
}

@Controller
interface EnumController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @Post(uri = "/enum")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        requestBody: EnumPostApplicationJsonRequestBody,
        @QueryValue(value = "p")
        p: EnumPostPParameters?
    ): HttpResponse<Unit>
}

@Controller
interface ObjectController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @Post(uri = "/object")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        requestBody: ObjectPostApplicationJsonRequestBody,
        @Valid @QueryValue(
            value =
            "p"
        )
        p: ObjectPostPParameters?
    ): HttpResponse<Unit>
}

@Controller
interface ArrayController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @Post(uri = "/array")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        requestBody: List<ArrayPostApplicationJsonRequestBody>,
        @Valid
        @QueryValue(value = "p")
        p: List<Int>?
    ): HttpResponse<Unit>
}
