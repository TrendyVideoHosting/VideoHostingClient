package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.models.Comment
import mikhail.shell.video.hosting.domain.models.CommentWithUser
import kotlin.time.Instant

data class CommentDto(
    @SerializedName("comment_id")
    val commentId: Long,
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("date_time")
    val dateTime: Instant,
    @SerializedName("text")
    val text: String,
)

data class CommentWithUserDto(
    @SerializedName("comment")
    val comment: CommentDto,
    @SerializedName("user")
    val user: UserDto
)

fun Comment.toDto() = CommentDto(
    commentId = commentId,
    videoId = videoId,
    userId = userId,
    dateTime = dateTime,
    text = text
)
fun CommentDto.toDomain() = Comment(
    commentId = commentId,
    videoId = videoId,
    userId = userId,
    dateTime = dateTime,
    text = text
)
fun CommentWithUser.toDto() = CommentWithUserDto(
    comment = comment.toDto(),
    user = user.toDto()
)
fun CommentWithUserDto.toDomain() = CommentWithUser(
    comment = comment.toDomain(),
    user = user.toDomain()
)
