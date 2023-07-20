package com.batuhan.management.data.model

import com.google.gson.annotations.SerializedName

data class AnalyticsDetailsResponse(
    @SerializedName("analyticsProperty") val analyticsProperty: AnalyticsProperty?
)

data class AnalyticsProperty(
    @SerializedName("id") val id: String?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("analyticsAccountId") val analyticsAccountId: String?
)
