package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.models.User

data class UserDto(
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("nick")
    val nick: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("bio")
    val bio: String?,
    @SerializedName("tel")
    val tel: String?,
    @SerializedName("email")
    val email: String?
)

fun User.toDto() = UserDto(
    userId = userId,
    nick = nick,
    name = name,
    bio = bio,
    tel = tel,
    email = email
)
fun UserDto.toDomain() = User(
    userId = userId,
    nick = nick,
    name = name,
    bio = bio,
    tel = tel,
    email = email
)