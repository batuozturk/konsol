package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DeviceFile(
    @SerializedName("obbFile") val obbFile: ObbFile?,
    @SerializedName("regularFile") val regularFile: RegularFile?
)

@Keep
data class ObbFile(
    @SerializedName("obbFileName") val obbFileName: String?,
    @SerializedName("obb") val obb: FileReference?
)

@Keep
data class RegularFile(
    @SerializedName("content") val content: FileReference?,
    @SerializedName("devicePath") val devicePath: String?
)

@Keep
data class FileReference(
    @SerializedName("gcsPath") val gcsPath: String?
)
