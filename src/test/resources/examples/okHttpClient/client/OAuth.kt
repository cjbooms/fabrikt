package examples.okHttpClient.client

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okio.IOException

class OAuth2 : Authenticator {
    var accessToken: String? = null

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? =
        response.request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
}