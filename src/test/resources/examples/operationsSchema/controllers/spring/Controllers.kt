package examples.operationsSchema.controllers

import examples.operationsSchema.models.ArrayPostApplicationJsonRequestBody
import examples.operationsSchema.models.EnumPostApplicationJsonRequestBody
import examples.operationsSchema.models.EnumPostPParameters
import examples.operationsSchema.models.ObjectPostApplicationJsonRequestBody
import examples.operationsSchema.models.ObjectPostPParameters
import examples.operationsSchema.models.SimplePathPParameters
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

@Controller
@Validated
@RequestMapping("")
interface SimpleController {
    /**
     *
     *
     * @param requestBody
     * @param pathP
     * @param p
     */
    @RequestMapping(
        value = ["/simple"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        requestBody: String,
        @Valid @RequestParam(value = "path_p", required = false)
        pathP: SimplePathPParameters?,
        @RequestParam(value = "p", required = false) p: Int?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface EnumController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @RequestMapping(
        value = ["/enum"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        requestBody: EnumPostApplicationJsonRequestBody,
        @RequestParam(
            value =
            "p",
            required = false
        ) p: EnumPostPParameters?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface ObjectController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @RequestMapping(
        value = ["/object"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        requestBody: ObjectPostApplicationJsonRequestBody,
        @Valid
        @RequestParam(value = "p", required = false)
        p: ObjectPostPParameters?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface ArrayController {
    /**
     *
     *
     * @param requestBody
     * @param p
     */
    @RequestMapping(
        value = ["/array"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        requestBody: List<ArrayPostApplicationJsonRequestBody>,
        @Valid
        @RequestParam(value = "p", required = false)
        p: List<Int>?
    ): ResponseEntity<Unit>
}
