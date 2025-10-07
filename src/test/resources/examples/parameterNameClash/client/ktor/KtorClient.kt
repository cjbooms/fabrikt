package examples.parameterNameClash.client

import examples.parameterNameClash.models.SomeObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.client.request.`header`
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlin.String
import kotlin.Unit

public object Example

public class ExampleClient(
    public val httpClient: HttpClient,
) {
    /**
     * Parameters:
     * 	 @param pathB
     * 	 @param queryB
     *
     * Returns:
     * 	[kotlin.Unit] if the request was successful.
     */
    public suspend fun getByPathB(
        pathB: String,
        queryB: String,
    ): GetByPathBResult {
        val url =
            buildString {
                append("""/example/$pathB""")
                val params =
                    buildList {
                        add("b=$queryB")
                    }
                if (params.isNotEmpty()) append("?").append(params.joinToString("&"))
            }

        val response =
            httpClient.`get`(url) {
                `header`("Accept", "application/json")
            }
        return if (response.status.isSuccess()) {
            GetByPathBResult.Success(response.body(), response)
        } else {
            GetByPathBResult.Error(response)
        }
    }

    /**
     * Parameters:
     * 	 @param bodySomeObject example
     * 	 @param querySomeObject
     *
     * Returns:
     * 	[kotlin.Unit] if the request was successful.
     */
    public suspend fun post(
        bodySomeObject: SomeObject,
        querySomeObject: String,
    ): PostResult {
        val url =
            buildString {
                append("""/example""")
                val params =
                    buildList {
                        add("someObject=$querySomeObject")
                    }
                if (params.isNotEmpty()) append("?").append(params.joinToString("&"))
            }

        val response =
            httpClient.post(url) {
                `header`("Accept", "application/json")
                `header`("Content-Type", "application/json")
                setBody(bodySomeObject)
            }
        return if (response.status.isSuccess()) {
            PostResult.Success(response.body(), response)
        } else {
            PostResult.Error(response)
        }
    }

    public sealed class GetByPathBResult {
        public data class Success(
            public val `data`: Unit,
            public val response: HttpResponse,
        ) : GetByPathBResult()

        public data class Error(
            public val response: HttpResponse,
        ) : GetByPathBResult()
    }

    public sealed class PostResult {
        public data class Success(
            public val `data`: Unit,
            public val response: HttpResponse,
        ) : PostResult()

        public data class Error(
            public val response: HttpResponse,
        ) : PostResult()
    }
}
