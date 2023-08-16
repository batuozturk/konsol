package com.batuhan.firestore.data.model

import com.google.gson.annotations.SerializedName

data class Document(
    @SerializedName("name") val name: String? = null,
    @SerializedName("fields") val fields: Map<String, Value>?
)

data class ListDocumentsResponse(
    @SerializedName("documents") val documents: List<Document>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
