package com.batuhan.testlab.data.model.devicecatalog

import androidx.annotation.Keep
import com.batuhan.testlab.data.model.matrix.AndroidDevice
import com.google.gson.annotations.SerializedName

@Keep
data class AndroidModel(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("manufacturer") val manufacturer: String?,
    @SerializedName("brand") val brand: String?,
    @SerializedName("codename") val codename: String?,
    @SerializedName("form") val form: DeviceForm?,
    @SerializedName("formFactor") val formFactor: DeviceFormFactor?,
    @SerializedName("perVersionInfo") val perVersionInfo: List<PerAndroidVersionInfo>?,
    @SerializedName("screenX") val screenX: Int?,
    @SerializedName("screenY") val screenY: Int?,
    @SerializedName("screenDensity") val screenDensity: Int?,
    @SerializedName("lowFpsVideoRecording") val lowFpsVideoRecording: Boolean?,
    @SerializedName("supportedVersionIds") val supportedVersionIds: List<String>?,
    @SerializedName("supportedAbis") val supportedAbis: List<String>?,
    @SerializedName("tags") val tags: List<String>?,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String?
)

@Keep
enum class DeviceForm {
    VIRTUAL,
    PHYSICAL,
    EMULATOR
}

@Keep
enum class DeviceFormFactor {
    PHONE,
    TABLET,
    WEARABLE
}

@Keep
data class PerAndroidVersionInfo(
    @SerializedName("versionId") val versionId: String?,
    @SerializedName("deviceCapacity") val deviceCapacity: DeviceCapacity?
)

@Keep
enum class DeviceCapacity {
    DEVICE_CAPACITY_UNSPECIFIED,
    DEVICE_CAPACITY_HIGH,
    DEVICE_CAPACITY_MEDIUM,
    DEVICE_CAPACITY_LOW,
    DEVICE_CAPACITY_NONE
}
fun AndroidModel.toAndroidDevice(): AndroidDevice {
    return AndroidDevice(
        androidModelId = id,
        name = name,
        manufacturer = manufacturer,
        thumbnailUrl = thumbnailUrl,
        screenY = screenY,
        screenX = screenX,
        brand = brand,
        form = form
    )
}