package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ApiService(
    @SerializedName("state") val state: ApiServiceState?
)

@Keep
enum class ApiServiceState {
    ENABLED, DISABLED
}
