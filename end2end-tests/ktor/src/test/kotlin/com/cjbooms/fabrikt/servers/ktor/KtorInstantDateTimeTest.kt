package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.InstantDateTimeController.Companion.instantDateTimeRoutes
import io.ktor.client.request.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.dataconversion.*
import io.ktor.server.testing.*
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.mockk.CapturingSlot
import io.mockk.slot
import kotlinx.datetime.Instant
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class KtorInstantDateTimeTest {

    @Test
    fun `handles correct date`() {
        val listCapturingSlot: CapturingSlot<List<Instant>?> = slot()
        val param2CapturingSlot: CapturingSlot<Instant?> = slot()

        testApplication {
            configure(installInstantConverter = true)

            routing {
                instantDateTimeRoutes(InstantDateTimeControllerImpl(listCapturingSlot, param2CapturingSlot))
            }

            val response = client.get("/instant-date-time?query_param2=2025-02-16T10:52:46Z")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(null, listCapturingSlot.captured)
            assertEquals(Instant.parse("2025-02-16T10:52:46Z"), param2CapturingSlot.captured)
        }
    }

    @Test
    fun `handles correct list of date`() {
        val listCapturingSlot: CapturingSlot<List<Instant>?> = slot()
        val param2CapturingSlot: CapturingSlot<Instant?> = slot()

        testApplication {
            configure(installInstantConverter = true)

            routing {
                instantDateTimeRoutes(InstantDateTimeControllerImpl(listCapturingSlot, param2CapturingSlot))
            }

            val response = client.get("/instant-date-time?explode_list_query_param=2025-02-16T10:52:46Z&explode_list_query_param=2025-02-16T11:52:46Z")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(listOf(Instant.parse("2025-02-16T10:52:46Z"), Instant.parse("2025-02-16T11:52:46Z")), listCapturingSlot.captured)
            assertEquals(null, param2CapturingSlot.captured)
        }
    }

    @Test
    fun `returns 400 when date is invalid`() {
        val listCapturingSlot: CapturingSlot<List<Instant>?> = slot()
        val param2CapturingSlot: CapturingSlot<Instant?> = slot()

        testApplication {
            configure(installInstantConverter = true)

            routing {
                instantDateTimeRoutes(InstantDateTimeControllerImpl(listCapturingSlot, param2CapturingSlot))
            }

            val response = client.get("/instant-date-time?query_param2=20-02-16T10:52:46Z")

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    @Test
    fun `returns 400 when Instant converter is missing`() {
        val listCapturingSlot: CapturingSlot<List<Instant>?> = slot()
        val param2CapturingSlot: CapturingSlot<Instant?> = slot()

        testApplication {
            configure(installInstantConverter = false)

            routing {
                instantDateTimeRoutes(InstantDateTimeControllerImpl(listCapturingSlot, param2CapturingSlot))
            }

            val response = client.get("/instant-date-time?query_param2=2025-02-16T10:52:46Z")

            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }

    private fun ApplicationTestBuilder.configure(installInstantConverter: Boolean) {
        install(ContentNegotiation) {
            json()
        }

        if (installInstantConverter) {
            install(DataConversion) {
                convert<Instant> {
                    decode { values ->
                        values.single().let { Instant.parse(it) }
                    }
                    encode { value ->
                        listOf((value).toString())
                    }
                }

                convert<List<Instant>> {
                    decode { values ->
                        values.map { Instant.parse(it) }
                    }
                    encode { value ->
                        listOf((value).toString())
                    }
                }
            }
        }
    }
}