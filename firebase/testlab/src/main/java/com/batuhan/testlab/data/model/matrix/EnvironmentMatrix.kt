package com.batuhan.testlab.data.model.matrix

import com.batuhan.testlab.data.model.devicecatalog.DeviceForm
import com.batuhan.testlab.data.model.devicecatalog.Locale
import com.batuhan.testlab.data.model.devicecatalog.Orientation
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EnvironmentMatrix(
    @SerializedName("androidDeviceList") val androidDeviceList: AndroidDeviceList?
)

data class AndroidDeviceList(
    @SerializedName("androidDevices") val androidDevices: List<AndroidDevice>?
)

data class AndroidDevice(
    @SerializedName("androidModelId")
    val androidModelId: String? = null,
    @SerializedName("androidVersionId")
    val androidVersionId: String? = null,
    @Transient @Expose(serialize = false, deserialize = false)
    val locale: Locale? = null,
    @SerializedName("locale")
    val localeString: String? = null,
    @Transient @Expose(serialize = false, deserialize = false)
    val orientation: Orientation? = null,
    @SerializedName("orientation")
    val orientationString: String? = null,
    @Transient @Expose(serialize = false, deserialize = false)
    val thumbnailUrl: String?,
    @Transient @Expose(serialize = false, deserialize = false)
    val screenX: Int?,
    @Transient @Expose(serialize = false, deserialize = false)
    val screenY: Int?,
    @Transient @Expose(serialize = false, deserialize = false)
    val name: String?,
    @Transient @Expose(serialize = false, deserialize = false)
    val brand: String?,
    @Transient @Expose(serialize = false, deserialize = false)
    val manufacturer: String?,
    @Transient @Expose(serialize = false, deserialize = false)
    val form: DeviceForm?
)
