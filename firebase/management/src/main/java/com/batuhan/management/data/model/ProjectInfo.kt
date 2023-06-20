package com.batuhan.management.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProjectInfoResponse(
    @SerializedName("projectInfo") val projectInfos: List<ProjectInfo>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

data class ProjectInfo(
    @SerializedName("project") val project: String?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("locationId") val locationId: String?,
    @Expose(serialize = false, deserialize = false) val prevPageToken: String?
)
