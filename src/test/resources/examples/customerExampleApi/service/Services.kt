package examples.customerExampleApi.service

import examples.customerExampleApi.models.Customer
import examples.customerExampleApi.models.DataAccessRequest
import examples.customerExampleApi.models.DeleteRequest
import java.net.URI
import kotlin.Int
import kotlin.Pair
import kotlin.String
import kotlin.collections.List

interface CustomersService {
    fun read(customerNumber: String, xFlowId: String?): Customer
}

interface DeleteRequestsService {
    fun query(
        limit: Int,
        xFlowId: String?,
        customerNumber: String?,
        cursor: String?
    ): List<DeleteRequest>

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
    ): List<DataAccessRequest>

    fun create(dataAccessRequest: DataAccessRequest, xFlowId: String?): Pair<URI, DataAccessRequest?>

    fun read(id: String, xFlowId: String?): DataAccessRequest

    fun update(
        dataAccessRequest: DataAccessRequest,
        id: String,
        xFlowId: String?
    ): DataAccessRequest
}
