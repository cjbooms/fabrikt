//
// This file was generated from an OpenAPI specification by Fabrikt.
// DO NOT EDIT. Any changes will be overwritten the next time the code is generated.
// To update, modify the specification and re-generate.
//
package examples.fileComment.controllers

import examples.fileComment.models.Pet
import io.micronaut.http.HttpResponse
import io.micronaut.http.`annotation`.Controller
import io.micronaut.http.`annotation`.Get
import io.micronaut.http.`annotation`.Produces
import kotlin.collections.List

@Controller
public interface PetsController {
  /**
   * List all pets
   */
  @Get(uri = "/pets")
  @Produces(value = ["application/json"])
  public fun listPets(): HttpResponse<List<Pet>>
}
