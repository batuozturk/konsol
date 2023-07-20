package com.batuhan.management.data.model

import com.batuhan.core.data.model.State
import com.google.gson.annotations.SerializedName

data class IosApp(
    @SerializedName("name") val name: String? = null,
    @SerializedName("appId") val appId: String? = null,
    @SerializedName("displayName") val displayName: String? = null,
    @SerializedName("projectId") val projectId: String? = null,
    @SerializedName("bundleId") val bundleId: String? = null,
    @SerializedName("appStoreId") val appStoreId: String? = null,
    @SerializedName("teamId") val teamId: String? = null,
    @SerializedName("apiKeyId") val apiKeyId: String? = null,
    @SerializedName("state") val state: State? = null,
    @SerializedName("etag") val etag: String? = null
)

data class IosAppResponse(
    @SerializedName("apps") val apps: List<IosApp>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
