package com.batuhan.management.data.model

import androidx.annotation.Keep

@Keep
data class UpdateBillingInfoRequest(
    val billingAccountName: String?
)
