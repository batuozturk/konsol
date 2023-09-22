package com.batuhan.core.data.model.management

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Operation(
    @SerializedName("name") val name: String?,
    @SerializedName("done") val done: Boolean?,
    @SerializedName("error") val error: Status?
)

@Keep
data class Status(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?
)
