package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.QueryParamsController.Companion.queryParamsRoutes
import com.example.models.EnumQueryParam
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.testing.*
import io.ktor.util.converters.*
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
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
            configure(withDataConversion = false)

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
        val errorCapturingSlot = slot<String?>()

        testApplication {
            configure(withDataConversion = false, errorCapturingSlot = errorCapturingSlot)

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Request parameter name is missing", errorCapturingSlot.captured)
        }
    }

    @Test
    fun `returns 200 when limit parameter is valid`() {
        val nameCapturingSlot: CapturingSlot<String?> = slot()
        val enumCapturingSlot: CapturingSlot<EnumQueryParam?> = slot()

        testApplication {
            configure(withDataConversion = true)

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
        val errorCapturingSlot = slot<String?>()

        testApplication {
            configure(withDataConversion = true, errorCapturingSlot = errorCapturingSlot)

            routing {
                queryParamsRoutes(QueryParametersControllerImpl(nameCapturingSlot, enumCapturingSlot))
            }

            val response = client.get("/query-params?name=valid&order=invalid")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Request parameter order couldn't be parsed/converted to EnumQueryParam", errorCapturingSlot.captured)
        }
    }

    private fun ApplicationTestBuilder.configure(
        withDataConversion: Boolean = true,
        errorCapturingSlot: CapturingSlot<String?>? = null,
    ) {
        install(ContentNegotiation) {
            json()
        }

        if (withDataConversion) {
            install(DataConversion) {
                convert<EnumQueryParam> {
                    decode { values ->
                        values.single().let { EnumQueryParam.fromValue(it) ?:  throw DataConversionException() }
                    }
                }
            }
        }

        install(StatusPages) {
            exception<BadRequestException> { call, cause ->
                errorCapturingSlot?.captured = cause.message
                call.respond(HttpStatusCode.BadRequest)
            }
        }
    }
}