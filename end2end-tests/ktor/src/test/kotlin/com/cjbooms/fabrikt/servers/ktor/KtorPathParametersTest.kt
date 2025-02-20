package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.PathParamsController.Companion.pathParamsRoutes
import com.example.models.PathParamWithEnum
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.dataconversion.DataConversion
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.CapturingSlot
import io.mockk.slot
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals

class KtorPathParametersTest {
    @Test
    fun `handles path parameters of different types using DataConversion plugin`() {
        val primitiveParamCapturingSlot = slot<String?>()
        val formatParamCapturingSlot = slot<UUID?>()
        val enumParamCapturingSlot = slot<PathParamWithEnum?>()

        testApplication {
            configure(withDataConversion = true)

            routing {
                pathParamsRoutes(PathParamsControllerImpl(
                    primitiveParamCapturingSlot,
                    formatParamCapturingSlot,
                    enumParamCapturingSlot
                ))
            }

            val response = client.get("/path-params/test/123e4567-e89b-12d3-a456-426614174000/active")

            assertEquals(HttpStatusCode.NoContent, response.status)
            assertEquals("test", primitiveParamCapturingSlot.captured)
            assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), formatParamCapturingSlot.captured)
            assertEquals(PathParamWithEnum.ACTIVE, enumParamCapturingSlot.captured)
        }
    }

    @Test
    fun `falls back to DefaultConversion service if DataConversion is not installed`() {
        val primitiveParamCapturingSlot = slot<String?>()
        val formatParamCapturingSlot = slot<UUID?>()
        val enumParamCapturingSlot = slot<PathParamWithEnum?>()

        testApplication {
            configure(withDataConversion = false)

            routing {
                pathParamsRoutes(PathParamsControllerImpl(
                    primitiveParamCapturingSlot,
                    formatParamCapturingSlot,
                    enumParamCapturingSlot
                ))
            }

            val response = client.get("/path-params/test/123e4567-e89b-12d3-a456-426614174000/ACTIVE") // N.B: DefaultConversionService matches on enum name which is uppercase

            assertEquals(HttpStatusCode.NoContent, response.status)
            assertEquals("test", primitiveParamCapturingSlot.captured)
            assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), formatParamCapturingSlot.captured)
            assertEquals(PathParamWithEnum.ACTIVE, enumParamCapturingSlot.captured)
        }
    }

    @Test
    fun `fails with 400 when conversion fails`() {
        val primitiveParamCapturingSlot = slot<String?>()
        val formatParamCapturingSlot = slot<UUID?>()
        val enumParamCapturingSlot = slot<PathParamWithEnum?>()
        val errorCapturingSlot = slot<String?>()

        testApplication {
            configure(withDataConversion = true, errorCapturingSlot = errorCapturingSlot)

            routing {
                pathParamsRoutes(PathParamsControllerImpl(
                    primitiveParamCapturingSlot,
                    formatParamCapturingSlot,
                    enumParamCapturingSlot
                ))
            }

            val response = client.get("/path-params/test/0000-invalid-uuid-0000/active")

            assertEquals(HttpStatusCode.BadRequest, response.status)
            assertEquals("Request parameter formatParam couldn't be parsed/converted to UUID", errorCapturingSlot.captured)
        }
    }

    private fun ApplicationTestBuilder.configure(
        withDataConversion: Boolean = true,
        errorCapturingSlot: CapturingSlot<String?>? = null,
    ) {
        if (withDataConversion) {
            install(DataConversion) {
                convert<UUID> {
                    decode { values ->
                        UUID.fromString(values.single())
                    }
                }
                convert<PathParamWithEnum> {
                    decode { values ->
                        PathParamWithEnum.entries.find { it.value == values.single() }
                            ?: throw BadRequestException("Invalid enum value ${values.single()}")
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