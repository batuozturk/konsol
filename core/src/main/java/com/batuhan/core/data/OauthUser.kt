package com.batuhan.core.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.openid.appauth.AuthState

@Entity
data class OauthUser(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("id")
    val id: Long? = null,
    @ColumnInfo("authState")
    val authState: AuthState
)
