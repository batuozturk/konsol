package com.batuhan.firestore.data.model

import com.google.gson.annotations.SerializedName

data class DocumentMask(
    @SerializedName("fieldPaths") val fieldPaths: List<String>?
)
