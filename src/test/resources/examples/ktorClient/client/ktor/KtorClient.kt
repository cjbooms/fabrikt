package examples.ktorClient.client

import examples.ktorClient.models.Item
import examples.ktorClient.models.SortOrder
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.client.request.`header`
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
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
        val url =
            buildString {
                append("""/items""")
                val params =
                    buildList {
                        limit?.let { add("limit=$it") }
                        category?.let { add("category=$it") }
                        priceLimit?.let { add("priceLimit=$it") }
                    }
                if (params.isNotEmpty()) append("?").append(params.joinToString("&"))
            }

        val response =
            httpClient.`get`(url) {
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
        val url =
            buildString {
                append("""/catalogs/$catalogId/items""")
                val params =
                    buildList {
                        add("randomNumber=$randomNumber")
                    }
                if (params.isNotEmpty()) append("?").append(params.joinToString("&"))
            }

        val response =
            httpClient.post(url) {
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
        val url = """/items/$itemId/subitems/$subItemId"""

        val response =
            httpClient.`get`(url) {
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
     * 	 @param listParam A list parameter
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
        listParam: List<String>? = null,
        xTracingID: String? = null,
    ): SearchCatalogItemsResult {
        val url =
            buildString {
                append("""/catalogs/$catalogId/search""")
                val params =
                    buildList {
                        add("query=$query")
                        page?.let { add("page=$it") }
                        sort?.let { add("sort=$it") }
                        listParam?.forEach { add("listParam=$it") }
                    }
                if (params.isNotEmpty()) append("?").append(params.joinToString("&"))
            }

        val response =
            httpClient.`get`(url) {
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
        val url = """/catalogs/$catalogId/items/$itemId/availability"""

        val response =
            httpClient.`get`(url) {
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
        val url = """/catalogs/$catalogId/items/$itemId/availability"""

        val response =
            httpClient.put(url) {
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

    public sealed class PutByCatalogIdAndItemIdResult {
        public data class Success(
            public val `data`: Unit,
            public val response: HttpResponse,
        ) : PutByCatalogIdAndItemIdResult()

        public data class Error(
            public val response: HttpResponse,
        ) : PutByCatalogIdAndItemIdResult()
    }
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
        val url = """/uptime"""

        val response =
            httpClient.`get`(url) {
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
}
