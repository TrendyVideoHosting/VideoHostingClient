package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.models.Liking
import mikhail.shell.video.hosting.domain.models.Video
import mikhail.shell.video.hosting.domain.models.VideoForUser
import mikhail.shell.video.hosting.domain.models.VideoWithChannel
import kotlin.time.Instant

data class VideoDto(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("channel_id")
    val channelId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("date_time")
    val dateTime: Instant,
    @SerializedName("views")
    val views: Long,
    @SerializedName("likes")
    val likes: Long,
    @SerializedName("dislikes")
    val dislikes: Long,
    @SerializedName("description")
    val description: String?,
)

fun Video.toDto() = VideoDto(
    videoId = videoId,
    channelId = channelId,
    title = title,
    dateTime = dateTime,
    views = views,
    likes = likes,
    dislikes = dislikes,
    description = description
)

fun VideoDto.toDomain() = Video(
    videoId = videoId,
    channelId = channelId,
    title = title,
    dateTime = dateTime,
    views = views,
    likes = likes,
    dislikes = dislikes,
    description = description
)

data class VideoForUserDto(
    @SerializedName("video_id")
    val videoId: Long,
    @SerializedName("channel_id")
    val channelId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("date_time")
    val dateTime: Instant,
    @SerializedName("views")
    val views: Long,
    @SerializedName("likes")
    val likes: Long,
    @SerializedName("dislikes")
    val dislikes: Long,
    @SerializedName("liking")
    val liking: Liking,
    @SerializedName("description")
    val description: String?
)

fun VideoForUser.toDto() = VideoForUserDto(
    videoId = videoId,
    channelId = channelId,
    title = title,
    dateTime = dateTime,
    views = views,
    likes = likes,
    liking = liking,
    dislikes = dislikes,
    description = description
)

fun VideoForUserDto.toDomain() = VideoForUser(
    videoId = videoId,
    channelId = channelId,
    title = title,
    dateTime = dateTime,
    views = views,
    likes = likes,
    dislikes = dislikes,
    liking = liking,
    description = description
)

data class VideoWithChannelDto(
    @SerializedName("video")
    val video: VideoDto,
    @SerializedName("channel")
    val channel: ChannelDto
)

fun VideoWithChannelDto.toDomain() = VideoWithChannel(
    video = video.toDomain(),
    channel = channel.toDomain()
)