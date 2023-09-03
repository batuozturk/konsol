package com.batuhan.core.data.model

import com.google.gson.annotations.SerializedName

data class DefaultBucket(
    @SerializedName("bucket") val bucket: BucketObject?
)
