package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.errors.FileError
import mikhail.shell.video.hosting.domain.errors.NumericError
import mikhail.shell.video.hosting.domain.errors.TextError

data class VideoUploadingErrorResponse(
    @SerializedName("title_error")
    val titleError: TextError?,
    @SerializedName("channel_id_error")
    val channelIdError: NumericError?,
    @SerializedName("description_error")
    val descriptionError: TextError?,
    @SerializedName("source_error")
    val sourceError: FileError?,
    @SerializedName("cover_error")
    val coverError: FileError?
)