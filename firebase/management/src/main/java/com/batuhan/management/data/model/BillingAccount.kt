package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BillingAccount(
    @SerializedName("name") val name: String?,
    @SerializedName("open") val open: Boolean?,
    @SerializedName("displayName") val displayName: String?
)

@Keep
data class BillingAccountResponse(
    @SerializedName("billingAccounts") val billingAccounts: List<BillingAccount>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)
