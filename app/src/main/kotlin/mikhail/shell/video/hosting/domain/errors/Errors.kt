package mikhail.shell.video.hosting.domain.errors

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class TextError: Error, Parcelable {
    @SerializedName("empty")
    EMPTY,
    @SerializedName("short")
    SHORT,
    @SerializedName("long")
    LONG,
    @SerializedName("exists")
    EXISTS,
    @SerializedName("not_exists")
    NOT_EXISTS,
    @SerializedName("not_correct")
    NOT_CORRECT,
    @SerializedName("not_valid")
    NOT_VALID,
    @SerializedName("pattern")
    PATTERN
}

@Parcelize
enum class FileError: Error, Parcelable {
    @SerializedName("not_found")
    NOT_FOUND,
    @SerializedName("empty")
    EMPTY,
    @SerializedName("large")
    LARGE,
    @SerializedName("not_supported")
    NOT_SUPPORTED,
    @SerializedName("not_valid")
    NOT_VALID
}

@Parcelize
enum class NumericError: Error, Parcelable {
    @SerializedName("empty")
    EMPTY,
    @SerializedName("low")
    LOW,
    @SerializedName("high")
    HIGH,
    @SerializedName("not_exists")
    NOT_EXISTS,
    @SerializedName("not_valid")
    NOT_VALID
}