package mikhail.shell.video.hosting.domain.models

import com.google.gson.annotations.SerializedName

enum class Liking {
    @SerializedName("liked")
    LIKED,
    @SerializedName("disliked")
    DISLIKED,
    @SerializedName("none")
    NONE
}