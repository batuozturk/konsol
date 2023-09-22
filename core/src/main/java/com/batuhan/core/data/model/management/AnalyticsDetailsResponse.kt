package com.batuhan.core.data.model.management

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AnalyticsDetailsResponse(
    @SerializedName("analyticsProperty") val analyticsProperty: AnalyticsProperty?
)

@Keep
data class AnalyticsProperty(
    @SerializedName("id") val id: String?,
    @SerializedName("displayName") val displayName: String?,
    @SerializedName("analyticsAccountId") val analyticsAccountId: String?
)
