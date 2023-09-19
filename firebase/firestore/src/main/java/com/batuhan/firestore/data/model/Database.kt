package com.batuhan.firestore.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Database(
    @SerializedName("name") val name: String? = null,
    @SerializedName("uid") val uid: String? = null,
    @SerializedName("type") val type: DatabaseType? = null,
    @SerializedName("createTime") val createTime: String? = null,
    @SerializedName("updateTime") val updateTime: String? = null,
    @SerializedName("locationId") val locationId: String? = null
)

@Keep
enum class DatabaseType {
    FIRESTORE_NATIVE, DATASTORE_MODE
}

@Keep
data class ListDatabaseResponse(
    @SerializedName("databases") val databases: List<Database>?
)