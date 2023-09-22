package com.batuhan.core.data.model.management

import androidx.annotation.Keep

@Keep
data class RemoveGoogleAnalyticsRequest(
    val analyticsPropertyId: String?
)
