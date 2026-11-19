package mikhail.shell.video.hosting.domain.models

import com.google.gson.annotations.SerializedName

enum class UserNameCheckPurpose {
    @SerializedName("sign_in")
    SIGN_IN,
    @SerializedName("sign_up")
    SIGN_UP,
    @SerializedName("reset")
    RESET
}