package mikhail.shell.video.hosting.presentation.navigation.common

import android.os.Parcelable
import androidx.navigation3.runtime.NavKey
import kotlinx.parcelize.Parcelize

@Parcelize
sealed interface Route: NavKey, Parcelable {
    @Parcelize
    data object Authentication: Route {
        @Parcelize
        data object SignIn: Route
        @Parcelize
        data object SignUp: Route {
            @Parcelize
            data object Request: Route
            @Parcelize
            data class Verification(val userName: String): Route
            @Parcelize
            data class Confirmation(val token: String): Route
        }
        @Parcelize
        data object Reset: Route {
            @Parcelize
            data object Request: Route
            @Parcelize
            data class Verification(
                val userId: Long,
                val userName: String
            ): Route
            @Parcelize
            data class Confirmation(val token: String): Route
        }
    }
    @Parcelize
    data object Subscriptions: Route {
        @Parcelize
        data object View: Route
    }
    @Parcelize
    data class User(val userId: Long): Route {
        @Parcelize
        data class Profile(val userId: Long): Route
        @Parcelize
        data object Edit: Route
        @Parcelize
        data object Settings: Route
        @Parcelize
        data object ChannelCreation: Route
        @Parcelize
        data object VideoUploading: Route
    }
    @Parcelize
    data class Video(val videoId: Long): Route {
        @Parcelize
        data class View(val videoId: Long): Route
        @Parcelize
        data class Edit(val videoId: Long): Route
    }
    @Parcelize
    data object Recommendations: Route {
        @Parcelize
        data object View: Route
    }
    @Parcelize
    data object Search: Route {
        @Parcelize
        data object View: Route
    }
    @Parcelize
    data class Channel(val channelId: Long): Route {
        @Parcelize
        data class View(val channelId: Long): Route
        @Parcelize
        data class Edit(val channelId: Long): Route
    }
}