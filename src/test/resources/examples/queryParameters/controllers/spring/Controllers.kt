package examples.queryParameters.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.`annotation`.Validated
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RequestMethod
import org.springframework.web.bind.`annotation`.RequestParam
import kotlin.String
import kotlin.Unit

@Controller
@Validated
@RequestMapping("")
public interface ExampleController {
    /**
     *
     *
     * @param a
     * @param b
     * @param inlineEnum
     */
    @RequestMapping(
        value = ["/example"],
        produces = [],
        method = [RequestMethod.GET],
    )
    public fun `get`(
        @RequestParam(value = "a", required = true) a: String,
        @RequestParam(value = "b", required = true) b: String,
        @RequestParam(value = "inline_enum.", required = false) inlineEnum: Any?,
    ): ResponseEntity<Unit>
}
