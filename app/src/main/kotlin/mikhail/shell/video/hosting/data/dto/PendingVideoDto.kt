package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName

data class PendingVideoDto(
    @SerializedName("tmp_id")
    val tmpId: String
)