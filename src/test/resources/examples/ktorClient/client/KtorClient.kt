package examples.ktorClient.client

import examples.ktorClient.models.Item
import examples.ktorClient.models.SortOrder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.`get`
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.`header`
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import io.ktor.resources.Resource
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.collections.List

public object Items

public class ItemsClient(
    public val httpClient: HttpClient,
) {
    /**
     * Retrieve a list of items
     *
     * Parameters:
     * 	 @param limit Maximum number of items to return
     * 	 @param category Filter items by category
     * 	 @param priceLimit Maximum price of items to return
     *
     * Returns:
     * 	[kotlin.collections.List<examples.ktorClient.models.Item>] if the request was successful.
     */
    public suspend fun getItems(
        limit: Int? = null,
        category: String? = null,
        priceLimit: Double? = null,
    ): GetItemsResult {
        val response =
            httpClient.`get`(
                GetItems(
                    limit = limit,
                    category = category,
                    priceLimit =
                    priceLimit,
                ),
            ) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            GetItemsResult.Success(response.body(), response)
        } else {
            GetItemsResult.Error(response)
        }
    }

    public sealed class GetItemsResult {
        public data class Success(
            public val `data`: List<Item>,
            public val response: HttpResponse,
        ) : GetItemsResult()

        public data class Error(
            public val response: HttpResponse,
        ) : GetItemsResult()
    }

    /**
     * Retrieve a list of items
     *
     * HTTP method: GET
     *
     * Response:
     * 	A successful request returns an HTTP 200 response with
     * [kotlin.collections.List<examples.ktorClient.models.Item>] in the response body.
     *
     * Request parameters:
     * 	 @param limit Maximum number of items to return
     * 	 @param category Filter items by category
     * 	 @param priceLimit Maximum price of items to return
     */
    @Resource("/items")
    public class GetItems(
        public val limit: Int? = null,
        public val category: String? = null,
        public val priceLimit: Double? = null,
    )
}

public object CatalogsItems

public class CatalogsItemsClient(
    public val httpClient: HttpClient,
) {
    /**
     * Create a new item
     *
     * Parameters:
     * 	 @param item The item to create
     * 	 @param catalogId The ID of the catalog
     * 	 @param randomNumber Just a test query param
     * 	 @param xRequestID Unique identifier for the request
     * 	 @param xTracingID Unique identifier for the tracing
     *
     * Returns:
     * 	[examples.ktorClient.models.Item] if the request was successful.
     */
    public suspend fun createItem(
        item: Item,
        catalogId: String,
        randomNumber: Int,
        xRequestID: String,
        xTracingID: String? = null,
    ): CreateItemResult {
        val response =
            httpClient.post(CreateItem(catalogId = catalogId, randomNumber = randomNumber)) {
                `header`("Accept", "application/json")
                `header`("Content-Type", "application/json")
                setBody(item)
                `header`("X-Request-ID", xRequestID)
                `header`("X-Tracing-ID", xTracingID)
            }
        return if (response.status.isSuccess()) {
            CreateItemResult.Success(response.body(), response)
        } else {
            CreateItemResult.Error(response)
        }
    }

    public sealed class CreateItemResult {
        public data class Success(
            public val `data`: Item,
            public val response: HttpResponse,
        ) : CreateItemResult()

        public data class Error(
            public val response: HttpResponse,
        ) : CreateItemResult()
    }

    /**
     * Create a new item
     *
     * HTTP method: POST
     *
     * Request body:
     * 	[examples.ktorClient.models.Item] The item to create
     *
     * Response:
     * 	A successful request returns an HTTP 201 response with [examples.ktorClient.models.Item] in
     * the response body.
     *
     * Request headers:
     * 	"X-Request-ID" (required) Unique identifier for the request
     * 	"X-Tracing-ID" (optional) Unique identifier for the tracing
     *
     * Request parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param randomNumber Just a test query param
     */
    @Resource("/catalogs/{catalogId}/items")
    public class CreateItem(
        public val catalogId: String,
        public val randomNumber: Int,
    )
}

public object ItemsSubitems

public class ItemsSubitemsClient(
    public val httpClient: HttpClient,
) {
    /**
     * Retrieve a specific subitem of an item
     *
     * Parameters:
     * 	 @param itemId The ID of the item
     * 	 @param subItemId The ID of the subitem
     *
     * Returns:
     * 	[examples.ktorClient.models.Item] if the request was successful.
     */
    public suspend fun getSubItem(
        itemId: String,
        subItemId: String,
    ): GetSubItemResult {
        val response =
            httpClient.`get`(GetSubItem(itemId = itemId, subItemId = subItemId)) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            GetSubItemResult.Success(response.body(), response)
        } else {
            GetSubItemResult.Error(response)
        }
    }

    public sealed class GetSubItemResult {
        public data class Success(
            public val `data`: Item,
            public val response: HttpResponse,
        ) : GetSubItemResult()

        public data class Error(
            public val response: HttpResponse,
        ) : GetSubItemResult()
    }

    /**
     * Retrieve a specific subitem of an item
     *
     * HTTP method: GET
     *
     * Response:
     * 	A successful request returns an HTTP 200 response with [examples.ktorClient.models.Item] in
     * the response body.
     *
     * Request parameters:
     * 	 @param itemId The ID of the item
     * 	 @param subItemId The ID of the subitem
     */
    @Resource("/items/{itemId}/subitems/{subItemId}")
    public class GetSubItem(
        public val itemId: String,
        public val subItemId: String,
    )
}

public object CatalogsSearch

public class CatalogsSearchClient(
    public val httpClient: HttpClient,
) {
    /**
     * Search for items
     *
     * Parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param query The search query
     * 	 @param page Page number
     * 	 @param sort Sort order
     * 	 @param xTracingID Unique identifier for the tracing
     *
     * Returns:
     * 	[kotlin.collections.List<examples.ktorClient.models.Item>] if the request was successful.
     */
    public suspend fun searchCatalogItems(
        catalogId: String,
        query: String,
        page: Int? = null,
        sort: SortOrder? = null,
        xTracingID: String? = null,
    ): SearchCatalogItemsResult {
        val response =
            httpClient.`get`(
                SearchCatalogItems(
                    catalogId = catalogId,
                    query = query,
                    page =
                    page,
                    sort = sort,
                ),
            ) {
                `header`("Accept", "application/json")
                `header`("X-Tracing-ID", xTracingID)
            }
        return if (response.status.isSuccess()) {
            SearchCatalogItemsResult.Success(response.body(), response)
        } else {
            SearchCatalogItemsResult.Error(response)
        }
    }

    public sealed class SearchCatalogItemsResult {
        public data class Success(
            public val `data`: List<Item>,
            public val response: HttpResponse,
        ) : SearchCatalogItemsResult()

        public data class Error(
            public val response: HttpResponse,
        ) : SearchCatalogItemsResult()
    }

    /**
     * Search for items
     *
     * HTTP method: GET
     *
     * Response:
     * 	A successful request returns an HTTP 200 response with
     * [kotlin.collections.List<examples.ktorClient.models.Item>] in the response body.
     *
     * Request headers:
     * 	"X-Tracing-ID" (optional) Unique identifier for the tracing
     *
     * Request parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param query The search query
     * 	 @param page Page number
     * 	 @param sort Sort order
     */
    @Resource("/catalogs/{catalogId}/search")
    public class SearchCatalogItems(
        public val catalogId: String,
        public val query: String,
        public val page: Int? = null,
        public val sort: SortOrder? = null,
    )
}

public object CatalogsItemsAvailability

public class CatalogsItemsAvailabilityClient(
    public val httpClient: HttpClient,
) {
    /**
     * Check item availability
     *
     * Parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param itemId The ID of the item
     *
     * Returns:
     * 	[kotlin.Unit] if the request was successful.
     */
    public suspend fun getByCatalogIdAndItemId(
        catalogId: String,
        itemId: String,
    ): GetByCatalogIdAndItemIdResult {
        val response =
            httpClient.`get`(GetByCatalogIdAndItemId(catalogId = catalogId, itemId = itemId)) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            GetByCatalogIdAndItemIdResult.Success(response.body(), response)
        } else {
            GetByCatalogIdAndItemIdResult.Error(response)
        }
    }

    /**
     * Update item availability
     *
     * Parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param itemId The ID of the item
     *
     * Returns:
     * 	[kotlin.Unit] if the request was successful.
     */
    public suspend fun putByCatalogIdAndItemId(
        catalogId: String,
        itemId: String,
    ): PutByCatalogIdAndItemIdResult {
        val response =
            httpClient.put(PutByCatalogIdAndItemId(catalogId = catalogId, itemId = itemId)) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            PutByCatalogIdAndItemIdResult.Success(response.body(), response)
        } else {
            PutByCatalogIdAndItemIdResult.Error(response)
        }
    }

    public sealed class GetByCatalogIdAndItemIdResult {
        public data class Success(
            public val `data`: Unit,
            public val response: HttpResponse,
        ) : GetByCatalogIdAndItemIdResult()

        public data class Error(
            public val response: HttpResponse,
        ) : GetByCatalogIdAndItemIdResult()
    }

    /**
     * Check item availability
     *
     * HTTP method: GET
     *
     * Response:
     * 	A successful request returns an HTTP 204 response with an empty body.
     *
     * Request parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param itemId The ID of the item
     */
    @Resource("/catalogs/{catalogId}/items/{itemId}/availability")
    public class GetByCatalogIdAndItemId(
        public val catalogId: String,
        public val itemId: String,
    )

    public sealed class PutByCatalogIdAndItemIdResult {
        public data class Success(
            public val `data`: Unit,
            public val response: HttpResponse,
        ) : PutByCatalogIdAndItemIdResult()

        public data class Error(
            public val response: HttpResponse,
        ) : PutByCatalogIdAndItemIdResult()
    }

    /**
     * Update item availability
     *
     * HTTP method: PUT
     *
     * Response:
     * 	A successful request returns an HTTP 204 response with an empty body.
     *
     * Request parameters:
     * 	 @param catalogId The ID of the catalog
     * 	 @param itemId The ID of the item
     */
    @Resource("/catalogs/{catalogId}/items/{itemId}/availability")
    public class PutByCatalogIdAndItemId(
        public val catalogId: String,
        public val itemId: String,
    )
}

public object Uptime

public class UptimeClient(
    public val httpClient: HttpClient,
) {
    /**
     * Get the uptime of the system
     *
     *
     * Returns:
     * 	[kotlin.String] if the request was successful.
     */
    public suspend fun `get_System-Uptime`(): `Get_System-UptimeResult` {
        val response =
            httpClient.`get`(`Get_System-Uptime`()) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            `Get_System-UptimeResult`.Success(response.body(), response)
        } else {
            `Get_System-UptimeResult`.Error(response)
        }
    }

    public sealed class `Get_System-UptimeResult` {
        public data class Success(
            public val `data`: String,
            public val response: HttpResponse,
        ) : `Get_System-UptimeResult`()

        public data class Error(
            public val response: HttpResponse,
        ) : `Get_System-UptimeResult`()
    }

    /**
     * Get the uptime of the system
     *
     * HTTP method: GET
     *
     * Response:
     * 	A successful request returns an HTTP 200 response with [kotlin.String] in the response body.
     */
    @Resource("/uptime")
    public class `Get_System-Uptime`
}
