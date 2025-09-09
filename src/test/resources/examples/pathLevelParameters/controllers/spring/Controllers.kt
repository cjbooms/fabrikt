package examples.pathLevelParameters.controllers

import org.springframework.http.HttpStatus
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
     */
    @RequestMapping(
        value = ["/example"],
        produces = [],
        method = [RequestMethod.GET],
    )
    public fun `get`(
        @RequestParam(value = "a", required = true) a: String,
        @RequestParam(
            value = "b",
            required = true,
        ) b: String,
    ): ResponseEntity<Unit>
}
