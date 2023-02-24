package examples.parameterNameClash.controllers

import examples.parameterNameClash.models.SomeObject
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import javax.validation.Valid

@Controller
interface ExampleController {
    /**
     *
     *
     * @param pathB
     * @param queryB
     */
    @Get(uri = "/example/{b}")
    fun getById(
        @PathVariable(value = "pathB") pathB: String,
        @QueryValue(value = "queryB")
        queryB: String
    ): HttpResponse<Unit>

    /**
     *
     *
     * @param bodySomeObject example
     * @param querySomeObject
     */
    @Post(uri = "/example")
    @Consumes(value = ["application/json"])
    fun post(
        @Body @Valid
        bodySomeObject: SomeObject,
        @QueryValue(value = "querySomeObject")
        querySomeObject: String
    ): HttpResponse<Unit>
}
