package com.batuhan.core.data.model.firestore

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListCollectionIdsResponse(
    @SerializedName("collectionIds") val collectionIds: List<String>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
