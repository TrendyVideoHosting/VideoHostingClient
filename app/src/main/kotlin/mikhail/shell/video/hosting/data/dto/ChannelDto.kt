package mikhail.shell.video.hosting.data.dto

import com.google.gson.annotations.SerializedName
import mikhail.shell.video.hosting.domain.models.Channel
import mikhail.shell.video.hosting.domain.models.ChannelForUser
import mikhail.shell.video.hosting.domain.models.Subscription

data class ChannelDto(
    @SerializedName("channel_id")
    val channelId: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("alias")
    val alias: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("subscribers")
    val subscribers: Long
)

fun Channel.toDto() = ChannelDto(
    channelId = channelId,
    ownerId = ownerId,
    title = title,
    alias = alias,
    description = description,
    subscribers = subscribers
)

fun ChannelDto.toDomain() = Channel(
    channelId = channelId,
    ownerId = ownerId,
    title = title,
    alias = alias,
    description = description,
    subscribers = subscribers
)

data class ChannelForUserDto(
    @SerializedName("channel_id")
    val channelId: Long,
    @SerializedName("owner_id")
    val ownerId: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("alias")
    val alias: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("subscribers")
    val subscribers: Long,
    @SerializedName("subscription")
    val subscription: Subscription
)

fun ChannelForUser.toDto() = ChannelForUserDto(
    channelId = channelId,
    ownerId = ownerId,
    title = title,
    alias = alias,
    description = description,
    subscribers = subscribers,
    subscription = subscription
)

fun ChannelForUserDto.toDomain() = ChannelForUser(
    channelId = channelId,
    ownerId = ownerId,
    title = title,
    alias = alias,
    description = description,
    subscribers = subscribers,
    subscription = subscription
)