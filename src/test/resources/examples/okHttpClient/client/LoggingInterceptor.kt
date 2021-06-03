package examples.okHttpClient.client

import okhttp3.Interceptor
import okhttp3.Response
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingInterceptor : Interceptor {

    private val logger: Logger = LoggerFactory.getLogger("GeneratedClient")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val t1 = System.nanoTime()
        logger.info("Client Request: $request")

        val response = chain.proceed(request)

        val t2 = System.nanoTime()
        logger.info("Client Response after ${(t2 - t1) / 100_000}ms: $response")

        return response
    }
}
