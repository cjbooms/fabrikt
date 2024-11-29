package com.cjbooms.fabrikt.servers.ktor

import com.example.controllers.DefaultController
import com.example.controllers.DefaultController.Companion.defaultRoutes
import com.example.controllers.NoneController
import com.example.controllers.NoneController.Companion.noneRoutes
import com.example.controllers.OptionalController
import com.example.controllers.OptionalController.Companion.optionalRoutes
import com.example.controllers.RequiredController
import com.example.controllers.RequiredController.Companion.requiredRoutes
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import io.mockk.slot
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KtorAuthenticationTest {

    @Nested
    inner class OptionalAuth {
        @Test
        fun `returns principal when auth Authorization is provided`() {
            val principalCaptureSlot = slot<UserIdPrincipal?>()

            testApplication {
                configure()

                routing {
                    optionalRoutes(object : OptionalController {
                        override suspend fun testPath(
                            testString: String,
                            call: ApplicationCall
                        ) {
                            principalCaptureSlot.captured = call.principal<UserIdPrincipal>()
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                val response = client.get("/optional?testString=test") {
                    header("Authorization", "Basic dGVzdDp0ZXN0") // just anything to trigger auth
                }

                assertTrue(principalCaptureSlot.captured is UserIdPrincipal)
                assertEquals("routeAuth", principalCaptureSlot.captured?.name)
            }
        }

        @Test
        fun `returns null principal when Authorization is not provided`() {
            val principalCaptureSlot = slot<UserIdPrincipal?>()

            testApplication {
                configure()

                routing {
                    optionalRoutes(object : OptionalController {
                        override suspend fun testPath(
                            testString: String,
                            call: ApplicationCall
                        ) {
                            principalCaptureSlot.captured = call.principal<UserIdPrincipal>()
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                client.get("/optional?testString=test")

                assertEquals(null, principalCaptureSlot.captured)
            }
        }
    }

    @Nested
    inner class RequiredAuth {
        @Test
        fun `returns principal when Authorization is provided`() {
            val principalCaptureSlot = slot<UserIdPrincipal?>()

            testApplication {
                configure()

                routing {
                    requiredRoutes(object : RequiredController {
                        override suspend fun testPath(testString: String, call: ApplicationCall) {
                            principalCaptureSlot.captured = call.principal<UserIdPrincipal>()
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                client.get("/required?testString=test") {
                    header("Authorization", "Basic dGVzdDp0ZXN0") // just anything to trigger auth
                }

                assertTrue(principalCaptureSlot.captured is UserIdPrincipal)
                assertEquals("routeAuth", principalCaptureSlot.captured?.name)
            }
        }

        @Test
        fun `returns Unauthorized when Authorization is not provided`() {
            val principalCaptureSlot = slot<UserIdPrincipal?>()

            testApplication {
                configure()

                routing {
                    requiredRoutes(object : RequiredController {
                        override suspend fun testPath(testString: String, call: ApplicationCall) {
                            principalCaptureSlot.captured = call.principal<UserIdPrincipal>() // should not get called
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                val response = client.get("/required?testString=test")

                assertEquals(HttpStatusCode.Unauthorized, response.status)
                assertFalse(principalCaptureSlot.isCaptured)
            }
        }
    }

    @Nested
    inner class NoAuth {
        @Test
        fun `returns OK status when Authorization is not provided`() {
            testApplication {
                configure()

                routing {
                    noneRoutes(object : NoneController {
                        override suspend fun testPath(testString: String, call: ApplicationCall) {
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                val response = client.get("/none?testString=test")

                assertEquals(HttpStatusCode.OK, response.status)
            }
        }
    }

    @Nested
    inner class DefaultAuth {
        @Test
        fun `uses Authorization from default (top level security in OpenAPI spec)`() {
            val principalCaptureSlot = slot<UserIdPrincipal?>()

            testApplication {
                configure()

                routing {
                    defaultRoutes(object : DefaultController {
                        override suspend fun testPath(testString: String, call: ApplicationCall) {
                            principalCaptureSlot.captured = call.principal<UserIdPrincipal>()
                            call.respond(HttpStatusCode.OK)
                        }
                    })
                }

                client.get("/default?testString=test") {
                    header("Authorization", "Basic dGVzdDp0ZXN0") // just anything to trigger auth
                }

                assertTrue(principalCaptureSlot.captured is UserIdPrincipal)
                assertEquals("defaultAuth", principalCaptureSlot.captured?.name)
            }
        }
    }

    private fun ApplicationTestBuilder.configure() {
        install(Authentication) {
            // used for explicit endpoint auth
            //
            // /testPath:
            //     get:
            //      operationId: testPath
            //      security:
            //        - BasicAuth: [ ]
            basic("BasicAuth") {
                validate {
                    UserIdPrincipal("routeAuth") // always authenticate
                }
            }

            // used for default auth
            // which uses top level security in OpenAPI spec
            basic("basicAuth") {
                validate {
                    UserIdPrincipal("defaultAuth") // always authenticate
                }
            }

            bearer("BearerAuth") {
                authenticate {
                    UserIdPrincipal("defaultAuth") // always authenticate
                }
            }
        }

        install(ContentNegotiation) {
            jackson()
        }

        install(StatusPages)
    }
}
