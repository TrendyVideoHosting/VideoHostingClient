package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.errors.FileError
import mikhail.shell.video.hosting.domain.errors.TextError

data class VideoEditingErrorResponse(
    @SerializedName("title_error")
    val titleError: TextError?,
    @SerializedName("description_error")
    val descriptionError: TextError?,
    @SerializedName("cover_error")
    val coverError: FileError?
)