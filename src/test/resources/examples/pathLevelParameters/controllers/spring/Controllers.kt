package examples.pathLevelParameters.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
@Validated
@RequestMapping("")
interface ExampleController {
    /**
     *
     *
     * @param a
     * @param b
     */
    @RequestMapping(
        value = ["/example"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun get(
        @RequestParam(value = "a", required = true) a: String,
        @RequestParam(
            value = "b",
            required =
            true
        ) b: String
    ): ResponseEntity<Unit>
}
