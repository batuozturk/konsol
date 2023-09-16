package com.batuhan.firestore.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DocumentMask(
    @SerializedName("fieldPaths") val fieldPaths: List<String>?
)
