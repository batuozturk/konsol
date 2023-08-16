package com.batuhan.firestore.data.model

import com.google.gson.annotations.SerializedName

data class ListCollectionIdsResponse(
    @SerializedName("collectionIds") val collectionIds: List<String>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
