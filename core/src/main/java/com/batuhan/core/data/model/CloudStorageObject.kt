package com.batuhan.core.data.model

import com.google.gson.annotations.SerializedName

data class CloudStorageObject(
    @SerializedName("name") val name: String?
)

data class CloudStorageObjectResponse(
    @SerializedName("items") val items: List<CloudStorageObject>?,
    @SerializedName("prefixes") val prefixes: List<String>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
