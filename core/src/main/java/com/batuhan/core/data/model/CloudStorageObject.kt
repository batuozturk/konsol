package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CloudStorageObject(
    @SerializedName("name") val name: String?
)

@Keep
data class CloudStorageObjectResponse(
    @SerializedName("items") val items: List<CloudStorageObject>?,
    @SerializedName("prefixes") val prefixes: List<String>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)