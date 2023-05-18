package examples.responsesSchema.controllers

import examples.responsesSchema.models.SuccessResponseJson
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@Validated
@RequestMapping("")
interface FooController {
    /**
     *
     */
    @RequestMapping(
        value = ["/foo"],
        produces = ["application/json"],
        method = [RequestMethod.POST]
    )
    fun post(): ResponseEntity<SuccessResponseJson>
}
