package examples.simpleRequestBody.controllers

import examples.simpleRequestBody.service.UploadService
import javax.validation.Valid
import kotlin.String
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@Validated
@RequestMapping("/api")
class UploadController(
    val service: UploadService
) {
    /**
     * Create a new Contributor
     */
    @RequestMapping(
        value = ["/upload"],
        produces = ["application/x-yaml"],
        method = [RequestMethod.POST],
        consumes = ["application/x-yaml"]
    )
    fun post(
        @RequestBody @Valid
        oASDocument: String
    ): ResponseEntity<String> = ResponseEntity
        .ok(service.create(oASDocument).second!!)
}
