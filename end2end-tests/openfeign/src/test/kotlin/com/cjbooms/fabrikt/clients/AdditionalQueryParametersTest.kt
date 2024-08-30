package com.cjbooms.fabrikt.clients

import com.example.client.ExamplePath1Client
import com.example.models.FirstModel
import com.example.models.QueryResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.common.ConsoleNotifier
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.marcinziolo.kotlin.wiremock.contains
import com.marcinziolo.kotlin.wiremock.get
import com.marcinziolo.kotlin.wiremock.like
import com.marcinziolo.kotlin.wiremock.returns
import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.okhttp.OkHttpClient
import java.net.ServerSocket
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdditionalQueryParametersTest {
    private val port: Int = ServerSocket(0).use { socket -> socket.localPort }
    private val wiremock: WireMockServer = WireMockServer(
        WireMockConfiguration.options().port(port).notifier(ConsoleNotifier(true)))
    private val mapper = ObjectMapper()
    private val examplePath1Client: ExamplePath1Client = Feign
        .builder()
        .client(OkHttpClient())
        .encoder(JacksonEncoder(mapper))
        .decoder(JacksonDecoder(mapper))
        .target(ExamplePath1Client::class.java, "http://localhost:$port")

    @BeforeEach
    fun setUp() {
        wiremock.start()
    }

    @AfterEach
    fun afterEach() {
        wiremock.resetAll()
        wiremock.stop()
    }

    @Test
    fun `additional query parameters are properly appended to requests`() {
        val expectedResponse = QueryResult(listOf(FirstModel(id = "the parameter was there!")))
        wiremock.get {
            urlPath like "/example-path-1"
            queryParams contains "unspecified_param" like "some_value"
        } returns {
            statusCode = 200
            body = mapper.writeValueAsString(expectedResponse)
        }
        val result = examplePath1Client.getExamplePath1(additionalQueryParameters = mapOf("unspecified_param" to "some_value"))
        Assertions.assertThat(result).isEqualTo(expectedResponse)
    }
}
