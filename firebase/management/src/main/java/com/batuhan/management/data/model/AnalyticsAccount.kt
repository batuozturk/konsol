package com.batuhan.management.data.model

import com.google.gson.annotations.SerializedName

data class AnalyticsAccountResponse(
    @SerializedName("items") val items: List<AnalyticsAccount>?
)

data class AnalyticsAccount(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String?
)
