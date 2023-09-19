package com.batuhan.testlab.data.model.matrix

import androidx.annotation.Keep
import com.batuhan.testlab.data.model.execution.DeviceFile
import com.google.gson.annotations.SerializedName

@Keep
data class TestSetup(
    @SerializedName("filesToPush") val filesToPush: List<DeviceFile>?,
    @SerializedName("directoriesToPull") val directoriesToPull: List<String>?,
    @SerializedName("account") val account: Account?
)

@Keep
data class Account(
    @SerializedName("googleAuto") val googleAuto: GoogleAuto?
)

@Keep
class GoogleAuto
