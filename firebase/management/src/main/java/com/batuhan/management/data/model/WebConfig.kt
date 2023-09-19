package com.batuhan.management.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class WebConfig(
    @SerializedName("projectId") val projectId: String?,
    @SerializedName("appId") val appId: String?,
    @SerializedName("databaseURL") val databaseURL: String?,
    @SerializedName("storageBucket") val storageBucket: String?,
    @SerializedName("locationId") val locationId: String?,
    @SerializedName("apiKey") val apiKey: String?,
    @SerializedName("authDomain") val authDomain: String?,
    @SerializedName("messagingSenderId") val messagingSenderId: String?,
    @SerializedName("measurementId") val measurementId: String?
)
