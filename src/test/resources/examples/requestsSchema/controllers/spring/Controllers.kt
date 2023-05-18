package examples.requestsSchema.controllers

import examples.requestsSchema.models.DummyRequest
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import javax.validation.Valid
import kotlin.Unit

@Controller
@Validated
@RequestMapping("")
interface FooController {
    /**
     *
     *
     * @param dummyRequest Request body
     */
    @RequestMapping(
        value = ["/foo"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        dummyRequest: DummyRequest
    ): ResponseEntity<Unit>
}
