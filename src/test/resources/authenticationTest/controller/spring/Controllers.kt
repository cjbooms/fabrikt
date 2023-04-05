package authenticationTest.controllers

import authenticationTest.models.TestDTO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import javax.validation.Valid
import kotlin.Unit

@Controller
@Validated
@RequestMapping("")
interface TestController {
    /**
     *
     *
     * @param testDTO
     */
    @RequestMapping(
        value = ["/test"],
        produces = [],
        method = [RequestMethod.GET]
    )
    fun testPath(
        @Valid @RequestParam(value = "testDTO", required = true)
        testDTO: TestDTO
    ):
            ResponseEntity<Unit>
}