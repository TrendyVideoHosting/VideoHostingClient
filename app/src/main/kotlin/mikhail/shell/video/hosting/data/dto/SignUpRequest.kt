package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName

data class UserCreationRequest(
    @SerializedName("nick")
    val nick: String,
    @SerializedName("password")
    val password: String
)