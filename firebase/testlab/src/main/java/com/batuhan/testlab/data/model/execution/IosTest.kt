package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

// stays for future use

data class IosTest(
    @SerializedName("iosAppInfo") val iosAppInfo: IosAppInfo?,
    @SerializedName("iosXcTest") val iosXcTest: IosXcTest?,
    @SerializedName("iosRoboTest") val iosRoboTest: IosRoboTest?,
    @SerializedName("iosTestLoop") val iosTestLoop: IosTestLoop?
)

data class IosAppInfo(
    @SerializedName("name") val name: String?
)

data class IosXcTest(
    @SerializedName("bundleId") val bundleId: String?,
    @SerializedName("xcodeVersion") val xcodeVersion: String?
)

data class IosRoboTest(
    @SerializedName("bundleId") val bundleId: String?
)

class IosTestLoop
