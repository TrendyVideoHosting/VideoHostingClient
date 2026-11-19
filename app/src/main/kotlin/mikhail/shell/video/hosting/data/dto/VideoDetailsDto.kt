package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.models.VideoWithChannelForUser

data class VideoDetailsDto(
    @SerializedName("video")
    val video: VideoForUserDto,
    @SerializedName("channel")
    val channel: ChannelForUserDto,
)

fun VideoDetailsDto.toDomain() = VideoWithChannelForUser(
    video = video.toDomain(),
    channel = channel.toDomain()
)


fun VideoWithChannelForUser.toDto() = VideoDetailsDto(
    video.toDto(),
    channel.toDto()
)