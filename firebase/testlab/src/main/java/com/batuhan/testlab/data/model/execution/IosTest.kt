package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

// stays for future use

@Keep
data class IosTest(
    @SerializedName("iosAppInfo") val iosAppInfo: IosAppInfo?,
    @SerializedName("iosXcTest") val iosXcTest: IosXcTest?,
    @SerializedName("iosRoboTest") val iosRoboTest: IosRoboTest?,
    @SerializedName("iosTestLoop") val iosTestLoop: IosTestLoop?
)

@Keep
data class IosAppInfo(
    @SerializedName("name") val name: String?
)

@Keep
data class IosXcTest(
    @SerializedName("bundleId") val bundleId: String?,
    @SerializedName("xcodeVersion") val xcodeVersion: String?
)

@Keep
data class IosRoboTest(
    @SerializedName("bundleId") val bundleId: String?
)

@Keep
class IosTestLoop
