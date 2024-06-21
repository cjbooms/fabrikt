package ie.zalando.controllers

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.`annotation`.Validated
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RequestMethod
import java.io.InputStream
import javax.validation.Valid

@Controller
@Validated
@RequestMapping("")
public interface BinaryDataController {
    /**
     *
     *
     * @param applicationOctetStream
     */
    @RequestMapping(
        value = ["/binary-data"],
        produces = ["application/octet-stream"],
        method = [RequestMethod.POST],
        consumes = ["application/octet-stream"],
    )
    public fun postBinaryData(@RequestBody @Valid applicationOctetStream: InputStream): ResponseEntity<InputStream>
}
