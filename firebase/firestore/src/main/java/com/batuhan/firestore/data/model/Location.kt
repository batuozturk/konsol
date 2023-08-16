package com.batuhan.firestore.data.model

import com.google.gson.annotations.SerializedName

data class FirestoreLocation(
    @SerializedName("locationId") val locationId: String?,
    @SerializedName("displayName") val displayName: String?
)

data class ListFirestoreLocationsResponse(
    @SerializedName("locations") val locations: List<FirestoreLocation>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
