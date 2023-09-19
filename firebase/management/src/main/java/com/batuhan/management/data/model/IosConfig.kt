package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class IosConfig(
    @SerializedName("configFilename") val configFilename: String?,
    @SerializedName("configFileContents") val configFileContents: String?
)
