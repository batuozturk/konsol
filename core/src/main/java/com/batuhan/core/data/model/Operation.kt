package com.batuhan.core.data.model

import com.google.gson.annotations.SerializedName

data class Operation(
    @SerializedName("name") val name: String?,
    @SerializedName("done") val done: Boolean?,
    @SerializedName("error") val error: Status?
)

data class Status(
    @SerializedName("code") val code: String?,
    @SerializedName("message") val message: String?
)
