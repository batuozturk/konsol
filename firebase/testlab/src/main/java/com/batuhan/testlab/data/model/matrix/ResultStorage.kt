package com.batuhan.testlab.data.model.matrix

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ResultStorage(
    @SerializedName("googleCloudStorage") val googleCloudStorage: GoogleCloudStorage?
)

@Keep
data class GoogleCloudStorage(
    @SerializedName("gcsPath") val gcsPath: String?
)
