package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class BucketObject(
    @SerializedName("name") val name: String?
)