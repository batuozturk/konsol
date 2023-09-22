package com.batuhan.core.data.model.management

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ProjectInfoResponse(
    @SerializedName("projectInfo") val projectInfos: List<ProjectInfo>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

@Keep
data class ProjectInfo(
    @SerializedName("project") val project: String?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("locationId") val locationId: String?,
    @Expose(serialize = false, deserialize = false) val prevPageToken: String?
)
