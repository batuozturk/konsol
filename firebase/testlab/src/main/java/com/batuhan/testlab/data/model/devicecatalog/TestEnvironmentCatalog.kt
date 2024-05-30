package com.batuhan.testlab.data.model.devicecatalog

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class TestEnvironmentCatalog(
    @SerializedName("androidDeviceCatalog") val androidDeviceCatalog: AndroidDeviceCatalog?
)

@Keep
data class AndroidDeviceCatalog(
    @SerializedName("models") val models: List<AndroidModel>?,
    @SerializedName("versions") val versions: List<AndroidVersion>?,
    @SerializedName("runtimeConfiguration") val runtimeConfiguration: AndroidRuntimeConfiguration?
)

@Keep
data class Locale(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("region") val region: String?,
    @SerializedName("tags") val tags: List<String>?
)

@Keep
data class Orientation(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("tags") val tags: List<String>?
)

@Keep
data class AndroidRuntimeConfiguration(
    @SerializedName("locales") val locales: List<Locale>?,
    @SerializedName("orientations") val orientations: List<Orientation>?
)

@Keep
data class AndroidVersion(
    @SerializedName("id") val id: String?,
    @SerializedName("versionString") val versionString: String?,
    @SerializedName("apiLevel") val apiLevel: Int?,
    @SerializedName("codeName") val codeName: String?
)
