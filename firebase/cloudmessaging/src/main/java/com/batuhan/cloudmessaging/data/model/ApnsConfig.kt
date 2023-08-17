package com.batuhan.cloudmessaging.data.model

import com.google.gson.annotations.SerializedName

// won't be used for now, stays for future use

data class ApnsConfig(
    @SerializedName("headers") val headers: Map<String, String>?
)
