package com.batuhan.core.data.model.firestore

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FirestoreLocation(
    @SerializedName("locationId") val locationId: String?,
    @SerializedName("displayName") val displayName: String?
)

@Keep
data class ListFirestoreLocationsResponse(
    @SerializedName("locations") val locations: List<FirestoreLocation>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
