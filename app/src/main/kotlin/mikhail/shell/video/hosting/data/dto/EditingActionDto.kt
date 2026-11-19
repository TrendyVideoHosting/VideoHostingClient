package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName

enum class EditingActionDto {
    @SerializedName("keep")
    KEEP,
    @SerializedName("remove")
    REMOVE,
    @SerializedName("edit")
    EDIT
}