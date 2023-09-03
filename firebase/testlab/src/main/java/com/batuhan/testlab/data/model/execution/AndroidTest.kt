package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

data class AndroidTest(
    @SerializedName("androidAppInfo") val androidAppInfo: AndroidAppInfo?,
    @SerializedName("androidInsturmentationTest") val androidInstrumentationTest: AndroidInstrumentationTest?,
    @SerializedName("androidRoboTest") val androidRoboTest: AndroidRoboTest?,
    @SerializedName("androidTestLoop") val androidTestLoop: AndroidTestLoop?
)

data class AndroidAppInfo(
    @SerializedName("name") val name: String?,
    @SerializedName("packageName") val packageName: String?,
    @SerializedName("versionName") val versionName: String?,
    @SerializedName("versionCode") val versionCode: String?
)

data class AndroidRoboTest(
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)

data class AndroidInstrumentationTest(
    @SerializedName("testApk") val testApk: FileReference?,
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("testPackageId") val testPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)

data class AndroidTestLoop(
    @SerializedName("appPackageId") val appPackageId: String?,
    @SerializedName("appApk") val appApk: FileReference?,
    @SerializedName("appBundle") val appBundle: AppBundle?
)
