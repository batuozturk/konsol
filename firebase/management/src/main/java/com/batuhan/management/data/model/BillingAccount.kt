package com.batuhan.management.data.model

import com.google.gson.annotations.SerializedName

data class BillingAccount(
    @SerializedName("name") val name: String?,
    @SerializedName("open") val open: Boolean?,
    @SerializedName("displayName") val displayName: String?
)

data class BillingAccountResponse(
    @SerializedName("billingAccounts") val billingAccounts: List<BillingAccount>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
