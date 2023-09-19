package com.batuhan.management.data.model

import androidx.annotation.Keep

@Keep
data class RemoveGoogleAnalyticsRequest(
    val analyticsPropertyId: String?
)
