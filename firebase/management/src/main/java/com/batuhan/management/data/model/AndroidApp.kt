package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.batuhan.core.data.model.State
import com.google.gson.annotations.SerializedName

@Keep
data class AndroidApp(
    @SerializedName("name") val name: String? = null,
    @SerializedName("appId") val appId: String? = null,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("projectId") val projectId: String? = null,
    @SerializedName("packageName") val packageName: String? = null,
    @SerializedName("apiKeyId") val apiKeyId: String? = null,
    @SerializedName("state") val state: State? = null,
    @SerializedName("sha1Hashes") val sha1Hashes: List<String>? = null,
    @SerializedName("sha256Hashes") val sha256Hashes: List<String>? = null,
    @SerializedName("etag") val etag: String? = null
)

@Keep
data class AndroidAppResponse(
    @SerializedName("apps") val apps: List<AndroidApp>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
