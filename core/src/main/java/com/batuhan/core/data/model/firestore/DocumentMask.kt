package com.batuhan.core.data.model.firestore

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DocumentMask(
    @SerializedName("fieldPaths") val fieldPaths: List<String>?
)