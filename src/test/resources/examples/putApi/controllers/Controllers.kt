package examples.putApi.controllers

import examples.putApi.models.Contributor
import examples.putApi.service.ContributorsService
import javax.validation.Valid
import kotlin.String
import kotlin.Unit
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
@Validated
@RequestMapping("")
class ContributorsController(
    val service: ContributorsService
) {
    /**
     * Update an existing Contributor
     *
     * @param id The unique id for entity
     * @param ifMatch The RFC7232 If-Match header field in a request requires the server to only
     * operate on the resource that matches at least one of the provided entity-tags. This allows clients
     * express a precondition that prevent the method from being applied if there have been any changes
     * to the resource.
     *
     */
    @RequestMapping(
        value = ["/contributors/{id}"],
        produces = [],
        method = [RequestMethod.PUT],
        consumes = ["application/json"]
    )
    fun putById(
        @RequestBody @Valid
        contributor: Contributor,
        @PathVariable(value = "id", required = true)
        id: String,
        @RequestHeader(value = "If-Match", required = true)
        ifMatch: String
    ): ResponseEntity<Unit> {
        service.update(contributor, id, ifMatch)
        return ResponseEntity.noContent().build()
    }
}
