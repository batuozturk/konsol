package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AndroidTest(
    @SerializedName("androidAppInfo") val androidAppInfo: AndroidAppInfo?,
    @SerializedName("androidInstrumentationTest") val androidInstrumentationTest: AndroidInstrumentationTest?,
    @SerializedName("androidRoboTest") val androidRoboTest: AndroidRoboTest?,
    @SerializedName("androidTestLoop") val androidTestLoop: AndroidTestLoop?
)

@Keep
data class AndroidAppInfo(
    @SerializedName("name") val name: String?,
    @SerializedName("packageName") val packageName: String?,
    @SerializedName("versionName") val versionName: String?,
    @SerializedName("versionCode") val versionCode: String?
)

@Keep
data class AndroidRoboTest(
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)

@Keep
data class AndroidInstrumentationTest(
    @SerializedName("testApk") val testApk: FileReference?,
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("testPackageId") val testPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)

@Keep
data class AndroidTestLoop(
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)
