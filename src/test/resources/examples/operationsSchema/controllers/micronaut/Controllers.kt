package examples.operationsSchema.controllers

import examples.operationsSchema.models.Paths1arrayPostRequestBodyContentApplication1jsonSchemaItems
import examples.operationsSchema.models.Paths1enumPostParameters0Schema
import examples.operationsSchema.models.Paths1enumPostRequestBodyContentApplication1jsonSchema
import examples.operationsSchema.models.Paths1objectPostParameters0Schema
import examples.operationsSchema.models.Paths1objectPostRequestBodyContentApplication1jsonSchema
import examples.operationsSchema.models.Paths1simpleParameters0Schema
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
     * @param paths1simplePostRequestBodyContentApplication1jsonSchema
     * @param pathP
     * @param p
     */
    @Post(uri = "/simple")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        paths1simplePostRequestBodyContentApplication1jsonSchema: String,
        @Valid @QueryValue(value = "path_p")
        pathP: Paths1simpleParameters0Schema?,
        @QueryValue(value = "p") p: Int?
    ): HttpResponse<Unit>
}

@Controller
interface EnumController {
    /**
     *
     *
     * @param paths1enumPostRequestBodyContentApplication1jsonSchema
     * @param p
     */
    @Post(uri = "/enum")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        paths1enumPostRequestBodyContentApplication1jsonSchema: Paths1enumPostRequestBodyContentApplication1jsonSchema,
        @QueryValue(value = "p") p: Paths1enumPostParameters0Schema?
    ): HttpResponse<Unit>
}

@Controller
interface ObjectController {
    /**
     *
     *
     * @param paths1objectPostRequestBodyContentApplication1jsonSchema
     * @param p
     */
    @Post(uri = "/object")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        paths1objectPostRequestBodyContentApplication1jsonSchema: Paths1objectPostRequestBodyContentApplication1jsonSchema,
        @Valid @QueryValue(value = "p")
        p: Paths1objectPostParameters0Schema?
    ): HttpResponse<Unit>
}

@Controller
interface ArrayController {
    /**
     *
     *
     * @param paths1arrayPostRequestBodyContentApplication1jsonSchema
     * @param p
     */
    @Post(uri = "/array")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        paths1arrayPostRequestBodyContentApplication1jsonSchema: List<Paths1arrayPostRequestBodyContentApplication1jsonSchemaItems>,
        @Valid @QueryValue(value = "p")
        p: List<Int>?
    ): HttpResponse<Unit>
}
