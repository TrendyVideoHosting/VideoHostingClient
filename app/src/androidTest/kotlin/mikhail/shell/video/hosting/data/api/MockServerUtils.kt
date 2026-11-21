package mikhail.shell.video.hosting.data.api

import mockwebserver3.RecordedRequest
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartReader
import okio.Buffer

val standardJsonHeader = Headers.headersOf("Content-Type", "application/json")

fun RecordedRequest.getJsonPart(name: String): String? {
    val contentType = headers["Content-Type"]
        ?.toMediaTypeOrNull() ?: return null

    if (contentType.type != "multipart" ||
        contentType.subtype != "form-data"
    ) return null

    val boundary = contentType.parameter("boundary")
        ?: return null

    val requestBody = body ?: return null

    MultipartReader(Buffer().write(requestBody), boundary).use { reader ->
        while (true) {
            val part = reader.nextPart() ?: return null

            val disposition = part.headers["Content-Disposition"] ?: continue

            val partName = Regex("""(?:^|;)\s*name="([^"]+)"""")
                .find(disposition)
                ?.groupValues?.get(1)

            val isJson = part.headers["Content-Type"]?.toMediaTypeOrNull()
                ?.let { it.type == "application" && it.subtype == "json" } == true

            if (partName == name && isJson) {
                return part.body.readUtf8()
            }
        }
    }
}