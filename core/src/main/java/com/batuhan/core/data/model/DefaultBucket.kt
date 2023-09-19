package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DefaultBucket(
    @SerializedName("bucket") val bucket: BucketObject?,
    @SerializedName("location") val location: String?
)