package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

data class DeviceFile(
    @SerializedName("obbFile") val obbFile: ObbFile?,
    @SerializedName("regularFile") val regularFile: RegularFile?
)

data class ObbFile(
    @SerializedName("obbFileName") val obbFileName: String?,
    @SerializedName("obb") val obb: FileReference?
)

data class RegularFile(
    @SerializedName("content") val content: FileReference?,
    @SerializedName("devicePath") val devicePath: String?
)

data class FileReference(
    @SerializedName("gcsPath") val gcsPath: String?
)
