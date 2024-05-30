package com.batuhan.realtimedatabase.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class DatabaseInstance(
    @SerializedName("type") val type: DatabaseType?,
    @SerializedName("name") val name: String?,
    @SerializedName("databaseUrl") val databaseUrl: String? = null,
    @SerializedName("project") val project: String? = null,
    @SerializedName("state") val state: State? = null

)

@Keep
data class DatabaseInstanceResponse(
    @SerializedName("instances") val instances: List<DatabaseInstance>?,
    @SerializedName("nextPageToken") val nextPageToken: String?
)

@Keep
enum class DatabaseType {
    DEFAULT_DATABASE,
    USER_DATABASE
}

@Keep
enum class State {
    ACTIVE,
    DISABLED,
    DELETED
}
