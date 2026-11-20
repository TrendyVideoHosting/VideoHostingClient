package mikhail.shell.video.hosting.domain.models

import com.google.gson.annotations.SerializedName

enum class Subscription {
    @SerializedName("subscribed")
    SUBSCRIBED,
    @SerializedName("not_subscribed")
    NOT_SUBSCRIBED
}