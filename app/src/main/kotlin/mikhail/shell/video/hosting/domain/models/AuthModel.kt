package mikhail.shell.video.hosting.domain.models

import com.google.gson.annotations.SerializedName

data class AuthModel(
    @SerializedName("token")
    val token: String,
    @SerializedName("user_id")
    val userId: Long
)