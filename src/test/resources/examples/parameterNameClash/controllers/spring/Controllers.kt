package examples.parameterNameClash.controllers

import examples.parameterNameClash.models.SomeObject
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Controller
@Validated
@RequestMapping("")
interface ExampleController {
    /**
     *
     *
     * @param pathB
     * @param queryB
     */
    @RequestMapping(
        value = ["/example/{b}"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun getById(
        @PathVariable(value = "pathB", required = true) pathB: String,
        @RequestParam(
            value =
            "queryB",
            required = true
        ) queryB: String
    ): ResponseEntity<Unit>

    /**
     *
     *
     * @param bodySomeObject example
     * @param querySomeObject
     */
    @RequestMapping(
        value = ["/example"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        bodySomeObject: SomeObject,
        @RequestParam(
            value = "querySomeObject",
            required = true
        ) querySomeObject: String
    ): ResponseEntity<Unit>
}
