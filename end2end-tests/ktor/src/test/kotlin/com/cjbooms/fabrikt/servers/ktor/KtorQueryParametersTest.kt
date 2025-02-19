package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.QueryParamsController.Companion.queryParamsRoutes
import com.example.models.EnumQueryParam
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.testing.*
import io.ktor.util.converters.*
import io.mockk.CapturingSlot
import io.mockk.slot
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class KtorQueryParametersTest {
    @Test
    fun `returns 200 when name required query parameter is present`() {
        val nameCapturingSlot: CapturingSlot<String?> = slot()
        val enumCapturingSlot: CapturingSlot<EnumQueryParam?> = slot()

        testApplication {
            install(ContentNegotiation) {
                json()
            }

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params?name=test")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("test", nameCapturingSlot.captured)
            assertEquals(null, enumCapturingSlot.captured)
        }
    }

    @Test
    fun `returns 400 when name required query parameter is missing`() {
        val nameCapturingSlot: CapturingSlot<String?> = slot()
        val enumCapturingSlot: CapturingSlot<EnumQueryParam?> = slot()

        testApplication {
            install(ContentNegotiation) {
                json()
            }

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params")

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun `returns 200 when limit parameter is valid`() {
        val nameCapturingSlot: CapturingSlot<String?> = slot()
        val enumCapturingSlot: CapturingSlot<EnumQueryParam?> = slot()

        testApplication {
            install(ContentNegotiation) {
                json()
            }

            install(DataConversion) {
                convert<EnumQueryParam> {
                    decode { values ->
                        values.single().let { EnumQueryParam.fromValue(it) ?:  throw DataConversionException() }
                    }
                }
            }

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params?name=test&order=asc")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals("test", nameCapturingSlot.captured)
            assertEquals(EnumQueryParam.ASC, enumCapturingSlot.captured)
        }
    }

    @Test
    fun `returns 400 when limit parameter is invalid`() {
        val nameCapturingSlot: CapturingSlot<String?> = slot()
        val enumCapturingSlot: CapturingSlot<EnumQueryParam?> = slot()

        testApplication {
            install(ContentNegotiation) {
                json()
            }

            install(DataConversion) {
                convert<EnumQueryParam> {
                    decode { values ->
                        values.single().let { EnumQueryParam.fromValue(it) ?:  throw DataConversionException() }
                    }
                }
            }

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params?order=invalid")

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }
}