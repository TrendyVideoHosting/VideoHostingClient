package mikhail.shell.video.hosting.domain.providers

import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    @SerialName("user_id")
    val userId: Long = 0,
    @SerialName("token")
    val token: String = ""
)

interface UserDetailsProvider {
    val userDetails: StateFlow<UserDetails>
    fun get(): UserDetails
    suspend fun save(userDetails: UserDetails)
    suspend fun remove()
}