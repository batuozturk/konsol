package com.batuhan.management.data.model

import com.google.gson.annotations.SerializedName

data class AnalyticsAccountResponse(
    @SerializedName("items") val items: List<AnalyticsAccount>?,
    @SerializedName("totalResults") val totalResults: Int?,
    @SerializedName("startIndex") val startIndex: Int?
)

data class AnalyticsAccount(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)
