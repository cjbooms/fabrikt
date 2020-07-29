package examples.customerExampleApi.controllers

import examples.customerExampleApi.models.Customer
import examples.customerExampleApi.models.DataAccessRequest
import examples.customerExampleApi.models.DataAccessRequestQueryResult
import examples.customerExampleApi.models.DeleteRequest
import examples.customerExampleApi.models.DeleteRequestQueryResult
import examples.customerExampleApi.service.CustomersService
import examples.customerExampleApi.service.DataAccessRequestsService
import examples.customerExampleApi.service.DeleteRequestsService
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import kotlin.Int
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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@Controller
@Validated
@RequestMapping("/api")
class CustomersController(
    val service: CustomersService
) {
    /**
     * Information about a customer.
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param customerNumber The unique customer number
     */
    @RequestMapping(
        value = ["/customers/{customer_number}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun getById(
        @PathVariable(value = "customer_number", required = true)
        customerNumber: String,
        @RequestHeader(value = "X-Flow-Id", required = false)
        xFlowId: String?
    ):
    ResponseEntity<Customer> {
        val svcResp = service.read(customerNumber, xFlowId)
        return ResponseEntity
            .ok()
            .body(svcResp)
    }
}

@Controller
@Validated
@RequestMapping("/api")
class DeleteRequestsController(
    val service: DeleteRequestsService
) {
    /**
     * Page through all the DeleteRequest resources matching the query filters
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param customerNumber Filters search results to those matching the customer number
     *
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    @RequestMapping(
        value = ["/delete-requests"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun get(
        @Min(1)
        @Max(100)
        @RequestParam(value = "limit", required = false, defaultValue = "10")
        limit: Int,
        @RequestHeader(value = "X-Flow-Id", required = false)
        xFlowId: String?,
        @RequestParam(value = "customer_number", required = false)
        customerNumber: String?,
        @RequestParam(value = "cursor", required = false)
        cursor: String?
    ): ResponseEntity<DeleteRequestQueryResult> {
        val svcResp = service.query(limit, xFlowId, customerNumber, cursor)
        return ResponseEntity
            .ok()
            .body(svcResp)
    }

    /**
     * Create a new DeleteRequest
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     */
    @RequestMapping(
        value = ["/delete-requests"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        deleteRequest: DeleteRequest,
        @RequestHeader(
            value = "X-Flow-Id",
            required = false
        )
        xFlowId: String?
    ): ResponseEntity<Unit> {
        val svcResp = service.create(deleteRequest, xFlowId)
        val uri =
            ServletUriComponentsBuilder.fromCurrentRequest().path("/${svcResp.id}").build().toUri()
        return ResponseEntity.created(uri).build()
    }

    /**
     * Get a DeleteRequest by ID
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param id The unique id for the resource
     */
    @RequestMapping(
        value = ["/delete-requests/{id}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun getById(
        @PathVariable(value = "id", required = true)
        id: String,
        @RequestHeader(
            value =
                "X-Flow-Id",
            required = false
        )
        xFlowId: String?
    ): ResponseEntity<DeleteRequest> {
        val svcResp = service.read(id, xFlowId)
        return ResponseEntity
            .ok()
            .body(svcResp)
    }

    /**
     * Update an existing DeleteRequest
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param id The unique id for the resource
     */
    @RequestMapping(
        value = ["/delete-requests/{id}"],
        produces = [],
        method = [RequestMethod.PUT],
        consumes = ["application/json"]
    )
    fun putById(
        @RequestBody @Valid
        deleteRequest: DeleteRequest,
        @PathVariable(value = "id", required = true)
        id: String,
        @RequestHeader(value = "X-Flow-Id", required = false)
        xFlowId: String?
    ): ResponseEntity<Unit> {
        service.update(deleteRequest, id, xFlowId)
        return ResponseEntity.noContent().build()
    }
}

@Controller
@Validated
@RequestMapping("/api")
class DataAccessRequestsController(
    val service: DataAccessRequestsService
) {
    /**
     * Page through all the DataAccessRequest resources matching the query filters
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param customerNumber Filters search results to those matching the customer number
     *
     * @param limit Upper bound for number of results to be returned from query api. A default limit
     * will be applied if this is not present
     *
     * @param cursor An encoded value which represents a point in the data from where pagination will
     * commence. The pagination direction (either forward or backward) will also be encoded within the
     * cursor value. The cursor value will always be generated server side
     *
     */
    @RequestMapping(
        value = ["/data-access-requests"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun get(
        @Min(1)
        @Max(100)
        @RequestParam(value = "limit", required = false, defaultValue = "10")
        limit: Int,
        @RequestHeader(value = "X-Flow-Id", required = false)
        xFlowId: String?,
        @RequestParam(value = "customer_number", required = false)
        customerNumber: String?,
        @RequestParam(value = "cursor", required = false)
        cursor: String?
    ): ResponseEntity<DataAccessRequestQueryResult> {
        val svcResp = service.query(limit, xFlowId, customerNumber, cursor)
        return ResponseEntity
            .ok()
            .body(svcResp)
    }

    /**
     * Create a new DataAccessRequest
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     */
    @RequestMapping(
        value = ["/data-access-requests"],
        produces = [],
        method = [RequestMethod.POST],
        consumes = ["application/json"]
    )
    fun post(
        @RequestBody @Valid
        dataAccessRequest: DataAccessRequest,
        @RequestHeader(
            value =
                "X-Flow-Id",
            required = false
        )
        xFlowId: String?
    ): ResponseEntity<Unit> {
        val svcResp = service.create(dataAccessRequest, xFlowId)
        val uri =
            ServletUriComponentsBuilder.fromCurrentRequest().path("/${svcResp.id}").build().toUri()
        return ResponseEntity.created(uri).build()
    }

    /**
     * Get a DataAccessRequest by ID
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param id The unique id for the resource
     */
    @RequestMapping(
        value = ["/data-access-requests/{id}"],
        produces = ["application/json"],
        method = [RequestMethod.GET]
    )
    fun getById(
        @PathVariable(value = "id", required = true)
        id: String,
        @RequestHeader(
            value =
                "X-Flow-Id",
            required = false
        )
        xFlowId: String?
    ): ResponseEntity<DataAccessRequest> {
        val svcResp = service.read(id, xFlowId)
        return ResponseEntity
            .ok()
            .body(svcResp)
    }

    /**
     * Update an existing DataAccessRequest
     *
     * @param xFlowId A custom header that will be passed onto any further requests and can be used
     * for diagnosing.
     *
     * @param id The unique id for the resource
     */
    @RequestMapping(
        value = ["/data-access-requests/{id}"],
        produces = [],
        method = [RequestMethod.PUT],
        consumes = ["application/json"]
    )
    fun putById(
        @RequestBody @Valid
        dataAccessRequest: DataAccessRequest,
        @PathVariable(value = "id", required = true)
        id: String,
        @RequestHeader(value = "X-Flow-Id", required = false)
        xFlowId: String?
    ): ResponseEntity<Unit> {
        service.update(dataAccessRequest, id, xFlowId)
        return ResponseEntity.noContent().build()
    }
}
