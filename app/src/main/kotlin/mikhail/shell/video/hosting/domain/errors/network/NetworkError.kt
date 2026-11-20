package mikhail.shell.video.hosting.domain.errors.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import mikhail.shell.video.hosting.domain.errors.Error

@Parcelize
enum class NetworkError: Error, Parcelable {
    @SerializedName("connection_error")
    CONNECTION_ERROR,
    @SerializedName("timeout_exceeded")
    TIMEOUT_EXCEEDED,
    @SerializedName("server_not_available")
    SERVER_NOT_AVAILABLE,
    @SerializedName("authentication")
    AUTHENTICATION,
    @SerializedName("forbidden")
    FORBIDDEN,
    @SerializedName("server_error")
    SERVER_ERROR,
    @SerializedName("not_found")
    NOT_FOUND,
    @SerializedName("bad_request")
    BAD_REQUEST,
    @SerializedName("conflict")
    CONFLICT,
    @SerializedName("many_requests")
    MANY_REQUESTS
}