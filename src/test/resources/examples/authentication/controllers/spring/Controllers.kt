package examples.authentication.controllers

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import kotlin.String
import kotlin.Unit

@Controller
@Validated
@RequestMapping("")
interface RequiredController {
    /**
     *
     *
     * @param testString
     */
    @RequestMapping(
        value = ["/required"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(
        @RequestParam(value = "testString", required = true) testString: String,
        authentication: Authentication
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface ProhibitedController {
    /**
     *
     *
     * @param testString
     */
    @RequestMapping(
        value = ["/prohibited"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(@RequestParam(value = "testString", required = true) testString: String):
        ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface OptionalController {
    /**
     *
     *
     * @param testString
     */
    @RequestMapping(
        value = ["/optional"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(
        @RequestParam(value = "testString", required = true) testString: String,
        authentication: Authentication?
    ): ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface NoneController {
    /**
     *
     *
     * @param testString
     */
    @RequestMapping(
        value = ["/none"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(@RequestParam(value = "testString", required = true) testString: String):
        ResponseEntity<Unit>
}

@Controller
@Validated
@RequestMapping("")
interface DefaultController {
    /**
     *
     *
     * @param testString
     */
    @RequestMapping(
        value = ["/default"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(
        @RequestParam(value = "testString", required = true) testString: String,
        authentication: Authentication
    ): ResponseEntity<Unit>
}
