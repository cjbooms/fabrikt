package examples.operationsSchema.controllers

import examples.operationsSchema.models.ArrayPostRequestBody
import examples.operationsSchema.models.EnumPostParametersP
import examples.operationsSchema.models.EnumPostRequestBody
import examples.operationsSchema.models.ObjectPostParametersP
import examples.operationsSchema.models.ObjectPostRequestBody
import examples.operationsSchema.models.SimpleParametersPathP
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
     * @param simplePostRequestBody
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
        simplePostRequestBody: String,
        @Valid @RequestParam(value = "path_p", required = false)
        pathP: SimpleParametersPathP?,
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
     * @param enumPostRequestBody
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
        enumPostRequestBody: EnumPostRequestBody,
        @RequestParam(
            value = "p",
            required = false
        ) p: EnumPostParametersP?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface ObjectController {
    /**
     *
     *
     * @param objectPostRequestBody
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
        objectPostRequestBody: ObjectPostRequestBody,
        @Valid
        @RequestParam(value = "p", required = false)
        p: ObjectPostParametersP?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface ArrayController {
    /**
     *
     *
     * @param arrayPostRequestBody
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
        arrayPostRequestBody: List<ArrayPostRequestBody>,
        @Valid
        @RequestParam(value = "p", required = false)
        p: List<Int>?
    ): ResponseEntity<Unit>
}
