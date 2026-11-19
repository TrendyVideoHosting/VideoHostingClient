package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.errors.FileError
import mikhail.shell.video.hosting.domain.errors.TextError

data class ChannelEditingErrorResponse(
    @SerializedName("title_error")
    val titleError: TextError?,
    @SerializedName("alias_error")
    val aliasError: TextError?,
    @SerializedName("description_error")
    val descriptionError: TextError?,
    @SerializedName("header_error")
    val headerError: FileError?,
    @SerializedName("logo_error")
    val logoError: FileError?
)
