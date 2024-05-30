package com.batuhan.core.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DefaultBucket(
    @SerializedName("name") val name: String?,
    @SerializedName("bucket") val bucket: BucketObject? = null,
    @SerializedName("location") val location: String?
)