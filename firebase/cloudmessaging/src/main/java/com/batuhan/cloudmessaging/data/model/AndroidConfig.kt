package com.batuhan.cloudmessaging.data.model

import com.google.gson.annotations.SerializedName

// won't be used for now, stays for future use

data class AndroidConfig(
    @SerializedName("priority") val androidMessagePriority: AndroidMessagePriority?,
    @SerializedName("ttl") val ttl: String,
    @SerializedName("restricted_package_name") val restrictedPackageName: String?,
    @SerializedName("notification") val androidNotification: AndroidNotification?,
)

enum class AndroidMessagePriority {
    NORMAL, HIGH
}

enum class NotificationPriority {
    PRIORITY_UNSPECIFIED, PRIORITY_MIN, PRIORITY_LOW, PRIORITY_DEFAULT, PRIORITY_HIGH, PRIORITY_MAX
}

enum class Visibility {
    VISIBILITY_UNSPECIFIED, PRIVATE, PUBLIC, SECRET
}

data class AndroidNotification(
    @SerializedName("channel_id") val channelId: String?,
    @SerializedName("sound") val sound: String?,
    @SerializedName("default_sound") val defaultSound: Boolean?,
    @SerializedName("notificationPriority") val notificationPriority: NotificationPriority?,
    @SerializedName("notificationCount") val notificationCount: Int?,
    @SerializedName("visibility") val visibility: Visibility?
)