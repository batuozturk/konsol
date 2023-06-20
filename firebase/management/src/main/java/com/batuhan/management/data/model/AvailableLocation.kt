package com.batuhan.management.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AvailableLocationResponse(
    @SerializedName("locations") val locations: List<AvailableLocation>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
data class AvailableLocation(
    @SerializedName("locationId") val locationId: String?,
    @SerializedName("type") val type: LocationType?,
    @SerializedName("features") val features: List<LocationFeature>?,
    @Expose(serialize = false, deserialize = false) val nextPageToken: String? = null
) {

    fun isFirestoreSupported() = features?.any { it.name == LocationFeature.FIRESTORE.name } ?: false

    fun isCloudFunctionsSupported() = features?.any { it.name == LocationFeature.FUNCTIONS.name } ?: false

    fun isDefaultCloudStorageBucketSupported() = features?.any { it.name == LocationFeature.DEFAULT_STORAGE.name } ?: false

    fun isMultiRegional() = type == LocationType.MULTI_REGIONAL
}

enum class LocationType {
    LOCATION_TYPE_UNSPECIFIED, REGIONAL, MULTI_REGIONAL
}

enum class LocationFeature {
    LOCATION_FEATURE_UNSPECIFIED, FIRESTORE, DEFAULT_STORAGE, FUNCTIONS
}
