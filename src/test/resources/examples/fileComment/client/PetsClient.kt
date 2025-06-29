//
// This file was generated from an OpenAPI specification by Fabrikt.
// DO NOT EDIT. Changes will be lost the next time the code is generated.
// Instead, update the spec and re-generate to update.
//
package examples.fileComment.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonTypeRef
import examples.fileComment.models.Pet
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.Throws

@Suppress("unused")
public class PetsClient(
    private val objectMapper: ObjectMapper,
    private val baseUrl: String,
    private val client: OkHttpClient,
) {
    /**
     * List all pets
     */
    @Throws(ApiException::class)
    public fun listPets(
        additionalHeaders: Map<String, String> = emptyMap(),
        additionalQueryParameters: Map<String, String> = emptyMap(),
    ): ApiResponse<List<Pet>> {
        val httpUrl: HttpUrl = "$baseUrl/pets"
            .toHttpUrl()
            .newBuilder()
            .also { builder -> additionalQueryParameters.forEach { builder.queryParam(it.key, it.value) } }
            .build()

        val headerBuilder = Headers.Builder()
        additionalHeaders.forEach { headerBuilder.header(it.key, it.value) }
        val httpHeaders: Headers = headerBuilder.build()

        val request: Request = Request.Builder()
            .url(httpUrl)
            .headers(httpHeaders)
            .get()
            .build()

        return request.execute(client, objectMapper, jacksonTypeRef())
    }
}
