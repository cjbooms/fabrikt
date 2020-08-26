package examples.customerExampleApi.service

import examples.customerExampleApi.models.Customer
import examples.customerExampleApi.models.DataAccessRequest
import examples.customerExampleApi.models.DataAccessRequestQueryResult
import examples.customerExampleApi.models.DeleteRequest
import examples.customerExampleApi.models.DeleteRequestQueryResult
import java.net.URI
import kotlin.Int
import kotlin.Pair
import kotlin.String

interface CustomersService {
    fun read(customerNumber: String, xFlowId: String?): Customer
}

interface DeleteRequestsService {
    fun query(
        limit: Int,
        xFlowId: String?,
        customerNumber: String?,
        cursor: String?
    ): DeleteRequestQueryResult

    fun create(deleteRequest: DeleteRequest, xFlowId: String?): Pair<URI, DeleteRequest?>

    fun read(id: String, xFlowId: String?): DeleteRequest

    fun update(
        deleteRequest: DeleteRequest,
        id: String,
        xFlowId: String?
    ): DeleteRequest
}

interface DataAccessRequestsService {
    fun query(
        limit: Int,
        xFlowId: String?,
        customerNumber: String?,
        cursor: String?
    ): DataAccessRequestQueryResult

    fun create(dataAccessRequest: DataAccessRequest, xFlowId: String?): Pair<URI, DataAccessRequest?>

    fun read(id: String, xFlowId: String?): DataAccessRequest

    fun update(
        dataAccessRequest: DataAccessRequest,
        id: String,
        xFlowId: String?
    ): DataAccessRequest
}
