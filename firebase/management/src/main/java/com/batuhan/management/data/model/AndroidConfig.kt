package com.batuhan.management.data.model

import com.google.gson.annotations.SerializedName

data class AndroidConfig(
    @SerializedName("configFilename") val configFilename: String?,
    @SerializedName("configFileContents") val configFileContents: String?
)
