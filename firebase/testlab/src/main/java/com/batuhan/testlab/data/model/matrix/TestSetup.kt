package com.batuhan.testlab.data.model.matrix

import com.batuhan.testlab.data.model.execution.DeviceFile
import com.google.gson.annotations.SerializedName

data class TestSetup(
    @SerializedName("filesToPush") val filesToPush: List<DeviceFile>?,
    @SerializedName("directoriesToPull") val directoriesToPull: List<String>?,
    @SerializedName("account") val account: Account?
)

data class Account(
    @SerializedName("googleAuto") val googleAuto: GoogleAuto?
)

class GoogleAuto
