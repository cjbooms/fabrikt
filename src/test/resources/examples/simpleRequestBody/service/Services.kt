package examples.simpleRequestBody.service

import kotlin.String

interface UploadService {
    fun create(oASDocument: String): String
}
