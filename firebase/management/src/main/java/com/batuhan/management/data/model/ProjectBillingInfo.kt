package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ProjectBillingInfo(
    @SerializedName("name") val name: String?,
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("billingAccountName") val billingAccountName: String?,
    @SerializedName("billingEnabled") val billingEnabled: Boolean?
)
