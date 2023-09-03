package com.batuhan.testlab.data.model.matrix

import com.google.gson.annotations.SerializedName

data class ResultStorage(
    @SerializedName("googleCloudStorage") val googleCloudStorage: GoogleCloudStorage?
)

data class GoogleCloudStorage(
    @SerializedName("gcsPath") val gcsPath: String?
)
