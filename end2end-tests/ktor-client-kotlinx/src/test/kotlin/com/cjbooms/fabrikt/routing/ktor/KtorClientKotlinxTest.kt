package com.cjbooms.fabrikt.routing.ktor

import com.example.client.CatalogsItemsAvailabilityClient
import com.example.client.CatalogsItemsClient
import com.example.client.CatalogsSearchClient
import com.example.client.UptimeClient
import com.example.models.Item
import com.example.models.SortOrder
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import com.marcinziolo.kotlin.wiremock.like
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returns
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.testing.testApplication
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertInstanceOf
import java.net.ServerSocket
import kotlin.test.assertEquals
import kotlin.test.fail

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KtorClientKotlinxTest {

    private val port: Int = ServerSocket(0).use { socket -> socket.localPort }

    private val wiremock: WireMockServer = WireMockServer(options().port(port).notifier(ConsoleNotifier(true)))

    @BeforeEach
    fun setUp() {
        wiremock.start()
    }

    @Nested
    inner class Client {
        @Test
        fun `client performs post with body`() {
            wiremock.post {
                urlPath like "/catalogs/catalog-a/items"
            } returns {
                statusCode = 201
                body = """
                    {
                        "id": "id-1",
                        "name": "item-a",
                        "description": "description-a",
                        "price": 123.45
                    }
                """.trimIndent()
                header = "Content-Type" to "application/json"
            }

            val httpClient = HttpClient(CIO) {
                install(Resources)
                install(ContentNegotiation) {
                    json()
                }
                defaultRequest {
                    url(wiremock.baseUrl())
                }
            }
            val client = CatalogsItemsClient(httpClient)

            runBlocking {
                val result = client.createItem(
                    item = Item(
                        id = "id-1",
                        name = "item-a",
                        description = "description-a",
                        price = 123.45
                    ),
                    catalogId = "catalog-a",
                    randomNumber = 123,
                    xRequestID = "request-id"
                )

                when (result) {
                    is CatalogsItemsClient.CreateItemResult.Success -> {
                        println("Created item with name: ${result.data.name}. Status code: ${result.response.status}")
                    }

                    is CatalogsItemsClient.CreateItemResult.Error -> {
                        fail("Failed to create item.\nStatus code: ${result.response.status}\nBody: ${result.response.bodyAsText()}")
                    }
                }
            }
        }

        @Test
        fun `client performs post and gets 404 back`() {
            wiremock.post {
                urlPath like "/catalogs/catalog-a/items"
            } returns {
                statusCode = 404
                body = "Not found"
            }

            val httpClient = HttpClient(CIO) {
                install(Resources)
                install(ContentNegotiation) {
                    json()
                }
                defaultRequest {
                    url(wiremock.baseUrl())
                }
            }
            val client = CatalogsItemsClient(httpClient)

            runBlocking {
                val result = client.createItem(
                    item = Item(
                        id = "id-1",
                        name = "item-a",
                        description = "description-a",
                        price = 123.45
                    ),
                    catalogId = "catalog-a",
                    randomNumber = 123,
                    xRequestID = "request-id"
                )

                when (result) {
                    is CatalogsItemsClient.CreateItemResult.Success -> {
                        fail("Expected 404 but got success")
                    }

                    is CatalogsItemsClient.CreateItemResult.Error -> {
                        println("Failed to create item. Status code: ${result.response.status}")
                    }
                }
            }
        }

        @Test
        fun `request can be performed using generated client`() = runBlocking {
            val capturedCatalogId = slot<String?>()
            val capturedQuery = slot<String?>()
            val capturedPage = slot<String?>()
            val capturedSort = slot<String?>()

            testApplication {
                routing {
                    get("/catalogs/{catalogId}/search") {
                        val catalogId = call.parameters["catalogId"]
                        val query = call.request.queryParameters["query"]
                        val page = call.request.queryParameters["page"]
                        val sort = call.request.queryParameters["sort"]

                        capturedCatalogId.captured = catalogId
                        capturedQuery.captured = query
                        capturedPage.captured = page
                        capturedSort.captured = sort

                        call.response.headers.append("Content-Type", "application/json")
                        call.respond("""
                        [
                            {
                                "id": "id-1",
                                "name": "item-a",
                                "description": "description-a",
                                "price": 123.45
                            }
                        ]
                    """.trimIndent())
                    }
                }

                val httpClient = createClient {
                    install(Resources)
                    install(ContentNegotiation) {
                        json()
                    }
                }

                val client = CatalogsSearchClient(httpClient)

                val response = client.searchCatalogItems("catalog-a", "query", 10, SortOrder.DESC)

                assertInstanceOf<CatalogsSearchClient.SearchCatalogItemsResult.Success>(response)
                assertEquals("catalog-a", capturedCatalogId.captured)
                assertEquals("query", capturedQuery.captured)
                assertEquals("10", capturedPage.captured)
                assertEquals("desc", capturedSort.captured)
            }
        }
    }

    @Nested
    inner class Resources {
        @Test
        fun `should generate URI with required params`() {
            val resFormat = ResourcesFormat()

            val uri = href(resFormat, CatalogsSearchClient.SearchCatalogItems("catalog-a", "query"))

            assertEquals("/catalogs/catalog-a/search?query=query", uri)
        }

        @Test
        fun `should generate URI with all params`() {
            val resFormat = ResourcesFormat()

            val uri = href(
                resFormat,
                CatalogsSearchClient.SearchCatalogItems(
                    catalogId = "catalog-a",
                    query = "query",
                    page = 1,
                    sort = SortOrder.ASC
                )
            )

            assertEquals(
                "/catalogs/catalog-a/search?query=query&page=1&sort=asc",
                uri
            )
        }

        @Test
        fun `resource name is generated correctly`() {
            val resFormat = ResourcesFormat()

            val uri = href(resFormat, CatalogsItemsAvailabilityClient.GetByCatalogIdAndItemId("catalog-a", "item-b"))

            assertEquals("/catalogs/catalog-a/items/item-b/availability", uri)
        }

        @Test
        fun `operationId with underscore and dash works`() {
            val resFormat = ResourcesFormat()

            val uri = href(resFormat, UptimeClient.`Get_System-Uptime`())

            assertEquals("/uptime", uri)
        }

        @Test
        fun `request can be performed using Ktor client and the Resources plugin`() = runBlocking {
            val capturedCatalogId = slot<String?>()
            val capturedQuery = slot<String?>()
            val capturedPage = slot<String?>()
            val capturedSort = slot<String?>()

            testApplication {
                routing {
                    get("/catalogs/{catalogId}/search") {
                        val catalogId = call.parameters["catalogId"]
                        val query = call.request.queryParameters["query"]
                        val page = call.request.queryParameters["page"]
                        val sort = call.request.queryParameters["sort"]

                        capturedCatalogId.captured = catalogId
                        capturedQuery.captured = query
                        capturedPage.captured = page
                        capturedSort.captured = sort

                        call.respond(HttpStatusCode.NoContent)
                    }
                }

                val client = createClient {
                    install(Resources)
                }

                val httpResponse = client.get(
                    CatalogsSearchClient.SearchCatalogItems(
                        catalogId = "catalog-a",
                        query = "query",
                        page = 10,
                        sort = SortOrder.DESC
                    )
                )

                assertTrue(httpResponse.status.isSuccess())
                assertEquals("catalog-a", capturedCatalogId.captured)
                assertEquals("query", capturedQuery.captured)
                assertEquals("10", capturedPage.captured)
                assertEquals("desc", capturedSort.captured)
            }
        }
    }
}
