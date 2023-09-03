package com.batuhan.testlab.data.model.execution

import com.google.gson.annotations.SerializedName

data class History(
    @SerializedName("historyId") val historyId: String?
)

data class HistoryListResponse(
    @SerializedName("histories") val histories: List<History>?
)
