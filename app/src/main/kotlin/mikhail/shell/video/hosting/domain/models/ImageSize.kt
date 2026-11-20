package mikhail.shell.video.hosting.domain.models

import com.google.gson.annotations.SerializedName

enum class ImageSize {
    @SerializedName("small")
    SMALL,
    @SerializedName("medium")
    MEDIUM,
    @SerializedName("large")
    LARGE
}