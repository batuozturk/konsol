package com.batuhan.core.data.model

import com.google.gson.annotations.SerializedName

data class ApiService(
    @SerializedName("state") val state: ApiServiceState?
)

enum class ApiServiceState {
    ENABLED, DISABLED
}
