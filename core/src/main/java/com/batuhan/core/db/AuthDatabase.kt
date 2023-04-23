package com.batuhan.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.batuhan.core.data.OauthUser

@Database(
    entities = [OauthUser::class],
    version = 1
)
@TypeConverters(
    AuthStateConverter::class
)
abstract class AuthDatabase : RoomDatabase() {

    abstract val authDao: AuthDao
}
