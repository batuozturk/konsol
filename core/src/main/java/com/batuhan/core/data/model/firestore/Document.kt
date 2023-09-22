package com.batuhan.core.data.model.firestore

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Document(
    @SerializedName("name") val name: String? = null,
    @SerializedName("fields") val fields: Map<String, Value>?
)

@Keep
data class ListDocumentsResponse(
    @SerializedName("documents") val documents: List<Document>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)