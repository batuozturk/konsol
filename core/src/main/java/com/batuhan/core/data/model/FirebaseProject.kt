package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class FirebaseProjectResponse(
    @SerializedName("results") val results: List<FirebaseProject>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

@Keep
data class FirebaseProject(
    @SerializedName("name") val name: String?,
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("projectNumber") val projectNumber: Long?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("resources") val resources: DefaultResources?,
    @SerializedName("state") val state: State?,
    @SerializedName("annotations") val annotations: Map<String, String?>?,
    @SerializedName("etag") val etag: String?,
    @Expose(serialize = false, deserialize = false) val prevPageToken: String?
)

@Keep
data class DefaultResources(
    @SerializedName("hostingSite") val hostingSite: String?,
    @SerializedName("realtimeDatabaseInstance") val realtimeDatabaseInstance: String?,
    @SerializedName("storageBucket") val storageBucket: String?,
    @SerializedName("locationId") val locationId: String?
)