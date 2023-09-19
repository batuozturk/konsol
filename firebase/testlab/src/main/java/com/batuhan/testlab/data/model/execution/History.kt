package com.batuhan.testlab.data.model.execution

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class History(
    @SerializedName("historyId") val historyId: String?
)

@Keep
data class HistoryListResponse(
    @SerializedName("histories") val histories: List<History>?
)
