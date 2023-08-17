package com.batuhan.cloudmessaging.data.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("name") val name: String? = null,
    @SerializedName("notification") val notification: Notification? = null,
    @SerializedName("androidConfig") val androidConfig: AndroidConfig? = null,
    @SerializedName("apnsConfig") val apnsConfig: ApnsConfig? = null,
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("token") val token: String? = null
)

data class Notification(
    @SerializedName("title") val title: String? = null,
    @SerializedName("body") val body: String? = null,
    @SerializedName("image") val imageUrl: String? = null
)

data class MessageRequest(
    val message: Message
)
