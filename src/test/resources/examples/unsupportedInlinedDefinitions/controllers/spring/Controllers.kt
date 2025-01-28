package examples.unsupportedInlinedDefinitions.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.`annotation`.Validated
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RequestMethod
import org.springframework.web.bind.`annotation`.RequestParam
import kotlin.Any
import kotlin.collections.List

@Controller
@Validated
@RequestMapping("")
public interface ExampleController {
    /**
     *
     *
     * @param inlineEnum
     * @param inlineObj
     */
    @RequestMapping(
        value = ["/example"],
        produces = ["application/json"],
        method = [RequestMethod.GET],
    )
    public fun `get`(
        @RequestParam(value = "inline_enum.", required = false) inlineEnum: Any?,
        @RequestParam(value = "inline_obj.", required = false) inlineObj: Any?,
    ): ResponseEntity<List<Any>>
}
