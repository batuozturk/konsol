package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

data class AppBundle(
    @SerializedName("bundleLocation") val bundleLocation: FileReference?
)
