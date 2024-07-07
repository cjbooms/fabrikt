package com.cjbooms.fabrikt.clients.jdk

import com.example.jdk_client.client.Url
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class UrlTest {

    @Test
    fun testExpansion() {
        val url = Url("http://bla.blub/{a}/test/{b}")
            .addPathParam("a", "123")
            .addPathParam("b", "abc")
            .addQueryParam("expand", "true")
            .addQueryParam("whatever", "654")

        assertThat(url.toUri())
            .isEqualTo(URI.create("http://bla.blub/123/test/abc?expand=true&whatever=654"))
    }

    @Test
    fun testWithoutQueryParams() {
        val url = Url("http://bla.blub/{a}/test/{b}")
            .addPathParam("a", "123")
            .addPathParam("b", "abc")

        assertThat(url.toUri())
            .isEqualTo(URI.create("http://bla.blub/123/test/abc"))
    }

    @Test
    fun testArrayWithoutExplode() {
        val url = Url("http://bla.blub/test")
            .addQueryParam("queryParam", listOf("a", "b"), true)

        assertThat(url.toUri())
            .isEqualTo(URI.create("http://bla.blub/test?queryParam=a&queryParam=b"))
    }

    @Test
    fun testArrayWithExplode() {
        val url = Url("http://bla.blub/test")
            .addQueryParam("queryParam", listOf("a", "b"), false)

        assertThat(url.toUri())
            .isEqualTo(URI.create("http://bla.blub/test?queryParam=a,b"))
    }
}