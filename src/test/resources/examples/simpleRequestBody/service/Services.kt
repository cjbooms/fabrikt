package examples.simpleRequestBody.service

import java.net.URI
import kotlin.Pair
import kotlin.String

interface UploadService {
    fun create(oASDocument: String): Pair<URI, String?>
}
