package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AnalyticsAccountResponse(
    @SerializedName("items") val items: List<AnalyticsAccount>?,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("startIndex") val startIndex: Int?
)

@Keep
data class AnalyticsAccount(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)
