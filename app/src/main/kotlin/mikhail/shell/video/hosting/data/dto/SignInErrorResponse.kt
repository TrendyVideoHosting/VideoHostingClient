package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.errors.TextError

data class SignInErrorResponse(
    @SerializedName("user_name_error")
    val userNameError: TextError?,
    @SerializedName("password_error")
    val passwordError: TextError?
)
