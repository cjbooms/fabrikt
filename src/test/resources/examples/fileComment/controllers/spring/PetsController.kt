//
// This file was generated from an OpenAPI specification by Fabrikt.
// DO NOT EDIT. Any changes will be overwritten the next time the code is generated.
// To update, modify the specification and re-generate.
//
package examples.fileComment.controllers

import examples.fileComment.models.Pet
import kotlin.collections.List
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.validation.`annotation`.Validated
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RequestMethod

@Controller
@Validated
@RequestMapping("")
public interface PetsController {
  /**
   * List all pets
   */
  @RequestMapping(
    value = ["/pets"],
    produces = ["application/json"],
    method = [RequestMethod.GET],
  )
  public fun listPets(): ResponseEntity<List<Pet>>
}
