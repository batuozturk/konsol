package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class AppBundle(
    @SerializedName("bundleLocation") val bundleLocation: FileReference?
)
